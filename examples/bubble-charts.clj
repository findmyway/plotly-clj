;; gorilla-repl.fileformat = 1

;; **
;;; #Bubble-Charts
;; **

;; **
;;; If you haven't read the **scatter.clj**, I strongly suggest you go through that file first.
;; **

;; @@
(ns bubble
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
;;; 
;; **

;; @@
(online-init)
;; @@
;; =>
;;; {"type":"html","content":"<script src=\"https://cdn.plot.ly/plotly-latest.min.js\" type=\"text/javascript\"></script>","value":"pr'ed value"}
;; <=

;; **
;;; ## Simple Bubble Chart
;; **

;; @@
(-> (plotly [10, 11, 12, 13])
    (add-scatter :mode "markers"
                 :text ["A<br>size: 40" "B<br>size: 60""C<br>size: 80""D<br>size: 100"]
                 :marker {:size [40, 60, 80, 100]
                          :color ["rgb(93, 164, 214)" "rgb(255, 144, 14)" "rgb(44, 160, 101)" "rgb(255, 65, 54)"]
                          :opacity [1 0.8 0.6 0.4]})
    (set-layout :xaxis {:zeroline false})
    (plot "simple-bubble-chart" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/36.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; **
;;; ## Scaling the Size and Text of Bubble Charts
;; **

;; @@
(-> (plotly {:x (vec (range 4)) 
             :y [10 11 12 13]
             :s [400 600 800 1000]})
    (add-scatter :mode "markers"
                 :marker {:size :s
                          :sizemode "area"})
    (add-scatter :mode "markers"
                 :y #(m/add (d/column % :y) 4)
                 :marker {:size #(m/mul (d/column % :s) 0.2)
                          :sizemode "area"})
    (add-scatter :mode "markers"
                 :y #(m/add (d/column % :y) 8)
                 :marker {:size #(m/mul (d/column % :s) 2)
                          :sizemode "area"})
    (plot "scaling-the-size-and-text-of-bubbe-charts" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/38.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; **
;;; ## Bubble Charts with Colorscale
;; **

;; @@
(-> (plotly {:x [1 3.2 5.4 7.6 9.8 12.5]
             :y [1 3.2 5.4 7.6 9.8 12.5]
             :colors [120 125 130 135 140 145]
             :sizes [15 30 55 70 90 110]})
    (add-scatter :mode "markers"
                 :marker {:size :sizes
                          :color :colors
                          :showscale true})
    (plot "bubble-charts-with-colorscale" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/40.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; @@
(def data 
  (let [in-file (slurp "https://raw.githubusercontent.com/plotly/datasets/master/gapminderDataFiveYear.csv")
        data (doall (csv/read-csv in-file))]
    (d/dataset (map keyword (first data)) (rest data))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;bubble/data</span>","value":"#'bubble/data"}
;; <=

;; @@
(defn parse-float 
  [x]
  (Float/parseFloat x))

(def data-2007 (d/dataset (map (fn [row] (-> row
                              (update :pop parse-float)
                              (update :lifeExp parse-float)
                              (update :gdpPercap parse-float)))
                (filter #(= "2007" (:year %)) (d/row-maps data)))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;bubble/data-2007</span>","value":"#'bubble/data-2007"}
;; <=

;; @@
(defn format-row
  [r]
  (format "Country: %s<br>Pop: %s <br>Life Expectancy: %s<br>GDP per capita: %s<br>"
          (first r) (nth r 2) (nth r 4) (nth r 5)))

(-> (plotly data-2007)
    (group-dataset :continent)
    (add-scatter :mode "markers"
                 :x :gdpPercap 
                 :y :lifeExp
                 :text (map format-row data-2007)
                 :marker {:symbol 0
                          :sizemode "diameter"
                          :sizeref 1.5
                          :size #(m/sqrt (m/mul (d/column % :pop) 2.666051223553066e-05))})
    (set-layout 
      :title "Life Expectancy v. Per Capita GDP, 2007"
      :xaxis {:title "GDP per capita (2000 dollars)"
              :type "log"
              :range [2.003297660701705 5.191505530708712]
              :zerolinewidth 1
              :ticklen 5
              :gridwidth 2}
      :yaxis {:title "Life Expectancy (years)"
              :range [36.12621671352166 91.72921793264332]
              :zerolinewidth 1
              :ticklen 5
              :gridwidth 2}
      :paper_bgcolor "rgb(243, 243, 243)"
      :plot_bgcolor "rgb(243, 243, 243)")
    (plot "Life-Expectancy-v.-Per-Capita-GDP,-2007" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/42.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; @@

;; @@
