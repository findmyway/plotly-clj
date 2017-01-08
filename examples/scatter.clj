;; gorilla-repl.fileformat = 1

;; **
;;; # SCATTER
;;; 
;;; This page shows almost all the examples related to scatter in [Plotly Python Library](https://plot.ly/python/) and [Plotly R Library](https://plot.ly/r/)
;; **

;; @@
(ns scatter
  (:require [clojure.core.matrix :as m]
            [clojure.core.matrix.dataset :as d]
            [clojure.core.matrix.random :as rnd]
            [clojure.data.csv :as csv])
  (:use [plotly-clj.core] :reload-all))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; **
;;; ## Initial
;;; 
;;; In order to play offline, we need to call ``offline-init`` first and it will insert the whole  ``plotly.min.js`` in this page. 
;;; However, this will make the page a little large. The size of ``plotly.min.js`` is about **1.7M** . To reduce the page size, I use the ``online-init`` instead here.
;; **

;; @@
(online-init) ;; Call offline-init here if you want to play offline
;; @@
;; =>
;;; {"type":"html","content":"<script src=\"https://cdn.plot.ly/plotly-latest.min.js\" type=\"text/javascript\"></script>","value":"pr'ed value"}
;; <=

;; **
;;; ##Simple Scatter Plot
;; **

;; @@
;; If you just want to play offline, just use ``iplot``
;;
;;(let [n 200]
;;  (-> (plotly (rnd/sample-normal n)
;;              (rnd/sample-normal n))
;;      (add-scatter :mode "markers")
;;      iplot))

;; In order to make the output more visual when viewed through gorilla-repl viewer.
;; I publish the figure to plot.ly server first and then embed the iframe here
(let [n 200]
  (-> (plotly (rnd/sample-normal n)
              (rnd/sample-normal n))
      (add-scatter :mode "markers")
      (plot "simple-scatter-plot" :fileopt "overwrite")
      embed-url))
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/20.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; **
;;; ## Line and Scatter Plots
;; **

;; @@
(let [n 100]
  (-> (plotly (rnd/sample-normal n))  
      (add-scatter :mode "lines+markers")
      (add-scatter 
        :y #(m/add 5 (d/column % 0)) ;; you can use a function which accept a dataset to create data 
        :mode "markers")
      (add-scatter 
        :y #(m/add -5 (d/column % :x))  ;; you can also use the default column name :x
        :mode "lines")
      (plot "line-and-scatter-plots" :fileopt "overwrite")
      embed-url))
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/24.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; **
;;; ## Style Scatter Plots
;; **

;; @@
(let [n 200]
  (-> (plotly)
      (add-scatter :x (rnd/sample-normal n) 
                   :y (m/add 2 (rnd/sample-normal n))
                   :mode "markers"
                   :name "above"
                   :marker {:size 10
                            :color "rgba(152, 0, 0, .8)"
                            :line {:width 2
                                   :color "rgb(0, 0, 0)"}})
      (add-scatter :x (rnd/sample-normal n)
                   :y (m/add -2 (rnd/sample-normal n))
                   :mode "markers"
                   :name "below"
                   :marker {:color "rgba(255, 182, 193, .9)"
                            :size 10
                            :line {:width 2}})
      (set-layout :xaxis {:zeroline false}
                  :yaxis {:zeroline false}
                  :title "Styled Scatter")
      (plot "style-scatter-plots"  :fileopt "overwrite")
      embed-url))
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/26.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; **
;;; ## Data Labels on Hover
;; **

;; @@
(def usa-states 
  (let [in-file (slurp "https://raw.githubusercontent.com/plotly/datasets/master/2014_usa_states.csv")
        data (doall (csv/read-csv in-file))]
    (d/dataset (map keyword (first data)) (rest data))
  ))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;scatter/usa-states</span>","value":"#'scatter/usa-states"}
;; <=

;; @@
(m/pm usa-states)
;; @@
;; ->
;;; [[:rank               :state :postal       :pop]
;;;  [    1              Alabama      AL  4849377.0]
;;;  [    2               Alaska      AK   736732.0]
;;;  [    3              Arizona      AZ  6731484.0]
;;;  [    4             Arkansas      AR  2966369.0]
;;;  [    5           California      CA 38802500.0]
;;;  [    6             Colorado      CO  5355866.0]
;;;  [    7          Connecticut      CT  3596677.0]
;;;  [    8             Delaware      DE   935614.0]
;;;  [    9 District of Columbia      DC   658893.0]
;;;  [   10              Florida      FL 19893297.0]
;;;  [   11              Georgia      GA 10097343.0]
;;;  [   12               Hawaii      HI  1419561.0]
;;;  [   13                Idaho      ID  1634464.0]
;;;  [   14             Illinois      IL 12880580.0]
;;;  [   15              Indiana      IN  6596855.0]
;;;  [   16                 Iowa      IA  3107126.0]
;;;  [   17               Kansas      KS  2904021.0]
;;;  [   18             Kentucky      KY  4413457.0]
;;;  [   19            Louisiana      LA  4649676.0]
;;;  [   20                Maine      ME  1330089.0]
;;;  [   21             Maryland      MD  5976407.0]
;;;  [   22        Massachusetts      MA  6745408.0]
;;;  [   23             Michigan      MI  9909877.0]
;;;  [   24            Minnesota      MN  5457173.0]
;;;  [   25          Mississippi      MS  2994079.0]
;;;  [   26             Missouri      MO  6063589.0]
;;;  [   27              Montana      MT  1023579.0]
;;;  [   28             Nebraska      NE  1881503.0]
;;;  [   29               Nevada      NV  2839098.0]
;;;  [   30        New Hampshire      NH  1326813.0]
;;;  [   31           New Jersey      NJ  8938175.0]
;;;  [   32           New Mexico      NM  2085572.0]
;;;  [   33             New York      NY 19746227.0]
;;;  [   34       North Carolina      NC  9943964.0]
;;;  [   35         North Dakota      ND   739482.0]
;;;  [   36                 Ohio      OH 11594163.0]
;;;  [   37             Oklahoma      OK  3878051.0]
;;;  [   38               Oregon      OR  3970239.0]
;;;  [   39         Pennsylvania      PA 12787209.0]
;;;  [   40          Puerto Rico      PR  3548397.0]
;;;  [   41         Rhode Island      RI  1055173.0]
;;;  [   42       South Carolina      SC  4832482.0]
;;;  [   43         South Dakota      SD   853175.0]
;;;  [   44            Tennessee      TN  6549352.0]
;;;  [   45                Texas      TX 26956958.0]
;;;  [   46                 Utah      UT  2942902.0]
;;;  [   47              Vermont      VT   626562.0]
;;;  [   48             Virginia      VA  8326289.0]
;;;  [   49           Washington      WA  7061530.0]
;;;  [   50        West Virginia      WV  1850326.0]
;;;  [   51            Wisconsin      WI  5757564.0]
;;;  [   52              Wyoming      WY   584153.0]]
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@
(defn parse-float 
  [x]
  (Float/parseFloat x))

(defn mark-usa
  [p i]
  (-> p
    (add-scatter :x :rank 
                 :y #(m/add (* i 1000000) (map parse-float (d/column % :pop)))
                 :mode "markers"
                 :marker {:size 14
                          :color (str "hsl(" (float (* i (/ 360 (count usa-states)))) ",50%,50%)")
                          :line {:width 1}
                          :opacity 0.3}
                 :name (+ 2000 i)
                 :text (nth (d/column usa-states :state) i))))

(-> (reduce mark-usa
            (plotly usa-states) 
            (range (count usa-states)))
    (set-layout :showlegend false
                :hovermode "closest")
    (plot "data-labels-on-hover" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/28.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; **
;;; ## Scatter with a Color Dimension
;; **

;; @@
(-> (plotly (rnd/sample-normal 100))
    (add-scatter :mode "markers" 
                 :marker {:size 16
                          :color :x
                          :colorscale "Viridis"
                          :showscale true})
    (plot "scatter-with-a-color-dimension" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/30.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; **
;;; ## Categorical Dot Plot
;; **

;; @@
(let [data {:country  ["Switzerland  (2011)"  "Chile  (2013)"  "Japan  (2014)"  "United States  (2012)"  "Slovenia  (2014)"  "Canada  (2011)"  "Poland  (2010)"  "Estonia  (2015)"  "Luxembourg  (2013)"  "Portugal  (2011)"]
            :voting_pop [40  45.7  52  53.6  54.1  54.2  54.5  54.7  55.1  56.6]
            :reg_voters [49.1  42  52.7  84.3  51.7  61.1  55.3  64.2  91.1  58.9]}]
  
  (-> (plotly data)
      (add-scatter
       :x :voting_pop
       :y :country
       :mode "markers"
       :name "Percent of estimated voting age population"
       :marker {:size 16
                :symbol "circle"
                :color "rgba(156, 165, 196, 0.95)" 
                :line {:color "rgba(156, 165, 196, 1.0)"
                       :width 1}})
      (add-scatter
        :x :reg_voters 
        :y :country 
        :name "Percent of estimated registered voters"
        :mode "markers"
        :marker {:color "rgba(204, 204, 204, 0.95)" 
                 :symbol "circle" 
                 :size 16 
                 :line {:color "rgba(217, 217, 217, 1.0)" 
                        :width 1}})
      (set-layout :title "Votes cast for ten lowest voting age population in OECD countries"
                 :xaxis {:showgrid false
                         :showline true
                         :linecolor "rgb(102, 102, 102)"
                         :titlefont {:color "rgb ()102,102,102"} 
                         :tickfont {:color "rgb(204, 204, 204)" 
                                    :autotick false :dtick 10 
                                    :ticks "outside" 
                                    :tickcolor "rgb(102, 102, 102)"}}
                 :margin {:l 140 :r 40 :b 50 :t 80}
                 :legend {:font {:size 10} 
                          :yanchor "middle"
                          :xanchor "right"}
                 :width 800
                 :height 600
                 :paper_bgcolor "rgb(254, 247, 234)"
                 :plot_bgcolor "rgb(254, 247, 234)"
                 :hovermode "closest")
      (plot "categorial-dot-plot"  :fileopt "overwrite")
      embed-url))
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/32.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; **
;;; ## Large Data Sets
;;; 
;;; Because the params of ``add-scatter`` will overwrite any predetermined settings, we can just set the ``:type`` to ``"scattergl"`` and then plotly will use WebGL instead of SVG. 
;;; 
;;; **Be careful to use this feature!**
;; **

;; @@
(let [n 10000]  
  (-> (plotly (rnd/sample-normal n) (rnd/sample-normal n))
      (add-scatter :mode "markers"
                   :type "scattergl"  ;; you can set a larger n and comment out this line,
                                      ;; then you will see the load difference
                   :marker {:color "FFBAD2"
                            :line {:width 1}})
      (plot "large-data-sets" :fileopt "overwrite")
      embed-url))
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/34.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; **
;;; ## Custom Color Scales
;; **

;; @@
(def iris 
  (let [in-file (slurp "https://raw.githubusercontent.com/uiuc-cse/data-fa14/gh-pages/data/iris.csv")
        data (doall (csv/read-csv in-file))]
    (d/dataset (map keyword (first data)) (rest data))
  ))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;scatter/iris</span>","value":"#'scatter/iris"}
;; <=

;; @@
(-> (plotly iris)
    (group-dataset :species)
    (add-scatter :x :sepal_length 
                 :y :petal_length
                 :mode "markers"
                 :gp-colors ["red" "green" "purple"]
                 :gp-symbols ["circle" "x" "circle-open"])
    (set-layout :hovermode "closest")
    (plot "custom-color-scales" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/22.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=
