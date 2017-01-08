(ns plotly-clj.core
  (:require [hiccup.core :refer [html]]
            [clojure.data.json :as json]
            [clojure.core.matrix :as m]
            [clojure.core.matrix.random :as rnd]
            [clojure.core.matrix.dataset :as d]
            [com.rpl.specter :as sp]
            [clojure.set :as s]
            [plotly-clj.scale :refer [scale-color factor]]
            [gorilla-renderable.core :as render]
            [clojure.java.io :refer [resource make-parents as-file]]
            [org.httpkit.client :as http]
            [clojure.java.browse :refer [browse-url]]
            [clojure.string :as string])
  (:import [java.io File]))

(declare group?)

(defn make-div
  "Make a div element given traces, layout and configs"
  [[traces layout configs]]
  (let [uuid (str (java.util.UUID/randomUUID))]
    (html [:div.plotly-graph-div {:id uuid}]
          [:script {:type "text/javascript"}
           (str "window.PLOTLYENV=window.PLOTLYENV || {};"
                "window.PLOTLYENV.BASE_URL=\"https://plot.ly\";"
                "Plotly.newPlot(\"" uuid "\"," traces "," layout "," configs ")")])))
(defn wrap-div
  "Wrap a div element in a html page"
  [div]
  (let [content (html [:html
                       [:head [:meta {:charset "utf-8"}]]
                       [:body
                        [:script {:type "text/javascript"}
                         (slurp (resource "plotly.min.js"))]
                        div]])]
    content))

(defn to-string
  "Format a plot object"
  [p]
  (map json/write-str (vals (select-keys p [:traces :layout :configs]))))

(defn- make-plotly
  "Initial a plotly object with dataset"
  [ds]
  {:ds ds :traces [] :layout {} :configs {}})

(defn- make-dataset
  "A flexiable function to create a dataset from different kinds of "
  ([] nil)
  ([x]
   (cond
     (nil? x) nil
     (d/dataset? x) x
     (group? x) x
     (= 1 (m/dimensionality x)) (d/dataset {:x x})
     ;; The x maybe a column-based map or row-based matrix.
     :else (d/dataset x)))
  ([x y]
   (d/dataset {:x x :y y}))
  ([x y z]
   (d/dataset {:x x :y y :z z})))

(defn plotly
  "Make a plotly object from:
      1. a vector (will be given name :x)
      2. two vectors (will be given name :x and :y)
      3. three vectors (will be given name :x, :y and :z)
      4. a dataset(clojure.core.matrix.dataset)
  TODO: add checkers
  "
  [& xs]
  (make-plotly (apply make-dataset xs)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn deep-merge  [a b]
  "Used to merge recursive maps"
  (merge-with  (fn  [x y]
                 (cond
                   (map? y)  (deep-merge x y)
                   (vector? y) (vec (concat x y))
                   :else y))
               a b))

(defn set-dataset
  "Set the dataset of a plotly object.
  xs is the same input as ``plotly``"
  [p & xs]
  (assoc p :ds (apply make-dataset xs)))

(defn set-layout
  "Set the layout of a plotly object"
  [p & {:as params}]
  (assoc p :layout params))

(defn update-layout
  "Update the layout of a plotly object"
  [p & {:as params}]
  (update p :layout #(deep-merge % params)))

(defn set-configs
  "Set configs of a plotly object"
  [p & {:as params}]
  (assoc p :configs params))

(defn update-configs
  "Update configs of a plotly object"
  [p & {:as params}]
  (update p :configs #(deep-merge % params)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- group-dataset-by
  "Helper function for group-dataset"
  [p ks]
  (let [group-idx (group-by #(nth ks %) (range (count (:ds p))))
        idx->dataset (fn [[k idx]] (vector k (d/select-rows (:ds p) idx)))]
    (assoc p :ds (into {} (map idx->dataset group-idx)))))

(defn group-dataset
  "Group a dataset based on k.
  k can be 
    1. a column name in dataset
    2. a sequence of column names 
    3. a seq which has the same length as the dataset
    4. a function takes dataset as input and 
       returns a seq which has the same length as the dataset
  TODO: 
    defmulti
    add checkers"
  [p k]
  (cond
    (keyword? k)  ;; a column name
    (group-dataset-by p (d/column (:ds p) k))
    (and (seq?) (every? keyword? k))  ;; a seq of column names
    (group-dataset-by p (d/select-columns (:ds p) k))
    (fn? k)
    (group-dataset-by p (k (:ds p)))
    :else
    (group-dataset-by p k)))

(defn group?
  "Check if dataset is a grouped dataset"
  [d]
  (and (map? d)
       (every? d/dataset? (vals d))))

(defn merge-group-dataset
  "Merge the dataset group of a plotly object into dataset"
  [p]
  (let [ds (:ds p)]
    (if (group? ds)
      (assoc p :ds (apply d/merge-datasets (vals ds)))
      p)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn trans-params
  "TODO: Add checkers.
  marker size must be numerical
  marker color can be numerical or categorized"
  [params dataset]
  (->> params

       (sp/transform
        (sp/multi-path
         [(sp/must :marker) (sp/must :size) keyword?]
         [(sp/must :x) keyword?]
         [(sp/must :y) keyword?])
        #(d/column dataset %))

       (sp/transform
        [(sp/must :marker) (sp/must :color) keyword?]
        #(factor (d/column dataset %)))

       (sp/transform
        (sp/multi-path
         [(sp/must :x) fn?]
         [(sp/must :y) fn?]
         [(sp/must :marker) (sp/must :size) fn?]
         [(sp/must :marker) (sp/must :color) fn?])
        #(% dataset))))

(defn trans-gp-params
  "Transform grouped params"
  [params ks]
  (let [gp-colors (or (:gp-colors params) (scale-color ks))
        gp-names (or (:gp-names params) ks)
        gp-symbols (or (:gp-symbols params) (range (count ks)))
        gp-texts (or (:gp-texts params) ks)]
    (for [i (range (count ks))]
      (-> params
          (dissoc :gp-colors :gp-names :gp-texts)
          (update :name #(or % (nth gp-names i)))
          (update :text #(or % (nth gp-texts i)))
          (update-in [:marker :color] #(or % (nth gp-colors i)))
          (update-in [:marker :symbol] #(or % (nth gp-symbols i)))
          (#(mapcat identity %))))))

(defn get-default-xy
  "Get default x and y from dataset.

  If the columns of dataset contains both :x and :y, 
  then the coresponding columns will be selected.
  Else the first two columns will be selected 
  (only if the column size of the dataset is >= 2).

  If the dataset contains only one column
  :y will be the only column and
  :x will be a vector of indexes."
  [dataset]
  (cond
    (s/subset? #{:x :y} (set (d/column-names dataset)))
    (d/to-map (d/select-columns dataset [:x :y]))
    (<= 2 (count (d/column-names dataset)))
    {:x (d/column dataset 0) :y (d/column dataset 1)}
    (= 1 (count (d/column-names dataset)))
    {:x (vec (range (count dataset))) :y (d/column dataset 0)}
    :else {}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn add-scatter
  "Add scatter trace to a plotly object.
  This function supports grouped dataset.
  "
  [{:keys [ds traces] :as p} & {:as params}]
  (cond
    (group? ds)
    (let [gp-ds (vec (vals ds))
          gp-params (vec (trans-gp-params params (keys ds)))
          p-new (reduce #(apply add-scatter
                                (assoc %1 :ds (nth gp-ds %2))
                                (nth gp-params %2))
                        p
                        (range (count ds)))]
      ;; recover dataset
      (set-dataset p-new ds))

    (nil? ds)
    (update p :traces #(conj % (merge {:type "scatter"} params)))

    :else
    (let [params (trans-params params ds)
          data (get-default-xy ds)
          trace (merge {:type "scatter"} data params)]
      (update p :traces #(conj % trace)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def cred-path (str (System/getProperty  "user.home") (File/separator) ".plotly" (File/separator) ".credentials"))

(defn set-credentials
  "Write the username and api-key into local file ~/.plotly/.credentials
  This function only needs to be called once."
  [username api-key]
  (let [cred {:username username :api-key api-key}]
    (when-not (.exists (as-file cred-path))
      (make-parents cred-path))
    (spit cred-path (json/write-str cred))))

(defn get-credentials
  "Read username and api-key from local file.
  The default credential file path is ~/.plotly/.credentials"
  []
  (json/read-str (slurp cred-path) :key-fn keyword))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn save-html
  "Save to local html."
  ([p filename] (save-html p filename nil))
  ([p filename is-open]
   (do (make-parents filename)
       (-> p
           to-string
           make-div
           wrap-div
           (#(spit filename %)))
       (when is-open (browse-url filename)))))

(defn plot
  "Publish to server. 
  Return the generated url of figure.
  TODO: Add support for api of V2"
  [{:keys [traces layout]}
   filename
   & {:keys [fileopt world-readable] :as params}]
  (let [{:keys [username api-key]} (get-credentials)
        data (merge {:origin "plot"
                     :platform "clojure"
                     :args (json/write-str traces)
                     :kwargs (json/write-str {:filename filename
                                              :fileopt (or fileopt "new")
                                              :layout layout
                                              :world_readable (if (nil? world-readable) true world-readable)})}
                    {:un username :key api-key})
        resp @(http/post  "https://plot.ly/clientresp" {:form-params data})]
    (:url (json/read-str (:body resp) :key-fn keyword))))

;;;;;;;;;;;;;;;;;;;;;;;;;;===== Gorilla REPL rendering  =====;;;;;;;;;;;;;;;;;;;;;;;;
(defrecord PlotlyView  [contents])

(defn plotly-view [p]  (PlotlyView. p))

(extend-type PlotlyView
  render/Renderable
  (render  [self]  {:type :html :content  (:contents self) :value "pr'ed value"}))

(defn online-init
  "Insert the plotly.min.js into gorilla repl"
  []
  (plotly-view
   (html [:script {:type "text/javascript"
                   :src "https://cdn.plot.ly/plotly-latest.min.js"}])))

(defn offline-init
  "Insert the plotly.min.js into gorilla repl"
  []
  (plotly-view
   (html [:script {:type "text/javascript"}
          (slurp (resource "plotly.min.js"))])))

(defn iplot
  "Inline plot"
  [p]
  (-> p
      to-string
      make-div
      plotly-view))

(defn embed-url
  [url & {:keys [width height]}]
  (plotly-view
   (html [:iframe {:width (or width "800")
                   :height (or height "600")
                   :src (str (string/replace-first url "https:" "")
                             ".embed")}])))
