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
                   (sequential? y) (vec (concat x y))
                   :else y))
               a b))

(defn set-dataset
  "Set the dataset of a plotly object.
  xs is the same input as ``plotly``"
  [p & xs]
  (assoc p :ds (apply make-dataset xs)))

(defn set-layout
  "Set the layout of a plotly object.
  This function will clear all the layout and reset the layout!
  So functions like `add-annotations` will not work if called before
  this function."
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
         [(sp/must :y) keyword?]
         [(sp/must :z) keyword?])
        #(d/column dataset %))

       (sp/transform
        [(sp/must :marker) (sp/must :color) keyword?]
        #(factor (d/column dataset %)))

       (sp/transform
        (sp/multi-path
         [(sp/must :x) fn?]
         [(sp/must :y) fn?]
         [(sp/must :z) fn?]
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
(defn plot-seq
  "Apply a sequence add-fn commands to a plotly object.
  cmds is a sequence of functions that take a plotly object as param."
  [p cmds]
  (reduce #(%2 %1) p cmds))

(def traces-3d #{"scatter3d" "surface" "mesh3d"})

(defn subplot
  "Bind each trace to a xaxis and a yaxis.
  This function supports only limited params currently.

  Example:
  traces: [trace1 trace2 trace3 trace4 trace5 trace6]
  nrow: 2
  ncol: 3
  x-bind(sharex true):
      [[1 2]
       [1 2]
       [1 2]]
  y-bind(sharey true):
      [[1 1]
       [2 2]
       [3 3]]
  x-bind(sharex false):
      [[1 2]
       [3 4]
       [5 6]]
  y-bind(sharey false):
      [[1 4]
       [2 5]
       [3 6]]

  x-domains: ([0 0.4][0.6 1][0 0.4][0.6 1][0 0.4][0.6 1])
  y-domains: ([0.7333 1][0.3666 0.6333][0 0.2666][0.7333 1][0.3666 0.6333][0 0.2666])
  TODO: Check nrow ncol zero?"
  [p & {:keys [nrow ncol sharex sharey reversey titles]}]
  (let [n (count (:traces p))
        [ncol nrow] (cond
                      (and ncol nrow) [ncol nrow]
                      ncol [ncol (inc (quot (dec n) ncol))]
                      nrow [(inc (quot (dec n) nrow)) nrow]
                      :else [1 n])
        x-bind (if sharex
                 (m/array (repeat nrow (range 1 (inc ncol))))
                 (m/reshape (range 1 (inc (* ncol nrow))) [nrow ncol]))
        y-bind (if sharey
                 (m/transpose (m/array (repeat ncol (range 1 (inc nrow)))))
                 (m/transpose (m/reshape (range 1 (inc (* ncol nrow))) [ncol nrow])))
        get-domains (fn [n] (let [gap (if (= 1 n) 0 (/ 0.1 (dec n)))
                                  span (if (= 1 n) 1 (/ 0.9 n))]
                              (map #(vector (* % (+ span gap))
                                            (+ (* gap %) (* span (inc %))))
                                   (range n))))
        x-domains (apply concat (repeat nrow (get-domains ncol)))
        y-domains (apply concat (repeat ncol (if reversey
                                               (get-domains nrow)
                                               (reverse (get-domains nrow)))))

        is-trace-3d (map #(traces-3d (:type %)) (:traces p))
        scenes (map-indexed (fn [i is-3d]
                              (when is-3d
                                (let [x (nth (m/eseq x-bind) i)
                                      y (nth (m/eseq y-bind) i)]
                                  {(keyword (str "scene" (inc i)))
                                   {:domain {:x (nth x-domains (dec x))
                                             :y (nth y-domains (dec y))}}})))
                            is-trace-3d)
        x-axis (map-indexed (fn [i is-3d]
                              (when-not is-3d
                                (let [x (nth (m/eseq x-bind) i)
                                      y (nth (m/eseq y-bind) i)]
                                  {(keyword (str "xaxis" x))
                                   {:domain (nth x-domains (dec x))
                                    :anchor (str "y" y)}})))
                            is-trace-3d)
        y-axis (map-indexed (fn [i is-3d]
                              (when-not is-3d
                                (let [x (nth (m/eseq x-bind) i)
                                      y (nth (m/eseq y-bind) i)]
                                  {(keyword (str "yaxis" y))
                                   {:domain (nth y-domains (dec y))
                                    :anchor (str "x" x)}})))
                            is-trace-3d)]
    (-> p
        (update :traces #(map (fn [t i x y]
                                (if (traces-3d (:type t))
                                  (assoc t :scene (str "scene" i))
                                  (-> t
                                      (assoc :xaxis (str "x" x))
                                      (assoc :yaxis (str "y" y)))))
                              % (range 1 (inc (count %))) (m/eseq x-bind) (m/eseq y-bind)))
        (update :layout #(apply merge % scenes))
        (update :layout #(apply merge % x-axis))
        ;; The reverse is needed here, which makes y-axis align to left if sharey is set.
        (update :layout #(apply merge % (reverse y-axis))))))

(defn add-annotations
  "This function will update the layout of a plotly object."
  [p ann]
  (cond
    (map? ann) ;; only one annotation
    (update-layout p :annotations [ann])
    (sequential? ann)
    (update-layout p :annotations ann)
    :else
    p))

(defn add-fn
  "Add general trace to a plotly object.
  This function supports grouped dataset.
  "
  [{:keys [ds traces] :as p}
   add-type
   params]
  (cond
    (group? ds)
    (let [gp-ds (vec (vals ds))
          gp-params (vec (trans-gp-params params (keys ds)))
          p-new (reduce #(apply add-fn
                                (assoc %1 :ds (nth gp-ds %2))
                                (nth gp-params %2))
                        p
                        (range (count ds)))]
      ;; recover dataset
      (set-dataset p-new ds))

    (nil? ds)
    (update p :traces #(conj % (merge {:type add-type} params)))

    :else
    (let [params (trans-params params ds)
          data (get-default-xy ds)
          trace (merge {:type add-type} data params)]
      (update p :traces #(conj % trace)))))

(def add-scatter (fn [p & {:as params}] (add-fn p "scatter" params)))
(def add-bar (fn [p & {:as params}] (add-fn p "bar" params)))
(def add-histogram (fn [p & {:as params}] (add-fn p "histogram" params)))
(def add-histogram-2d (fn [p & {:as params}] (add-fn p "histogram2d" params)))

(defn add-fn-3d
  "Add general 3d trace."
  [{:keys [ds traces] :as p}
   add-type
   params]
  (let [params (trans-params params ds)
        trace (merge {:type add-type} params)]
    (update p :traces #(conj % trace))))

(def add-scatter-3d (fn [p & {:as params}] (add-fn-3d p "scatter3d" params)))
(def add-surface (fn [p & {:as params}] (add-fn-3d p "surface" params)))
(def add-mesh3d (fn [p & {:as params}] (add-fn-3d p "mesh3d" params)))
(def add-contour (fn [p & {:as params}] (add-fn-3d p "contour" params)))
(def add-heatmap (fn [p & {:as params}] (add-fn-3d p "heatmap" params)))

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
        resp @(http/post  "https://plot.ly/clientresp" {:form-params data})
        res (json/read-str (:body resp) :key-fn keyword)]
    (let [url (:url res)]
      (if (empty? url)
        (:error res)
        url))))

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
