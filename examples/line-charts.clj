;; gorilla-repl.fileformat = 1

;; **
;;; # Line-Charts
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

;; @@
(online-init)
;; @@
;; =>
;;; {"type":"html","content":"<script src=\"https://cdn.plot.ly/plotly-latest.min.js\" type=\"text/javascript\"></script>","value":"pr'ed value"}
;; <=

;; @@
(-> (plotly (rnd/sample-normal 500))
    (add-scatter)
    (plot "simple-line-charts")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/44.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; @@
(def data
  (d/dataset
  {:month ["January" "February" "March" "April" "May" "June" "July" "August" "September" "October" "November" "December"]
   :high-2000 [32.5 37.6 49.9 53.0 69.1 75.4 76.5 76.6 70.7 60.6 45.1 29.3]
   :low-2000  [13.8 22.3 32.5 37.2 49.9 56.1 57.7 58.3 51.2 42.8 31.6 15.9]
   :high-2007  [36.5 26.6 43.6 52.3 71.5 81.4 80.5 82.2 76.0 67.3 46.1 35.0]
   :low-2007  [23.6 14.0 27.0 36.8 47.6 57.7 58.9 61.2 53.3 48.5 31.0 23.6]
   :high-2014  [28.8 28.5 37.0 56.8 69.7 79.7 78.5 77.8 74.1 62.6 45.3 39.9]
   :low-2014  [12.7 14.3 18.6 35.5 49.9 58.0 60.0 58.6 51.7 45.2 32.2 29.1]}))

(let [colors (flatten (repeat 3 ["rgb(205, 12, 24)" "rgb(22, 96, 167)"]))
      line-types ["dashdot" "dashdot" "dash" "dash" "dot" "dot"]
      cols (map #(d/column-name data %) (range 1 7))]
  (-> (plotly data)
      (plot-seq 
        (for [[col color line-type] (map vector cols colors line-types)]
          #(add-scatter %
                        :x :month
                        :y col
                        :name (str col)
                        :line {:color color :width 4 :dash line-type})))
      (plot "Average-High-and-Low-Temperatures-in-New-York" :fileopt "overwrite")
      embed-url))
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/46.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; @@
(-> (plotly [10 20 nil 15 10 5 15 nil 20 10 10 15 25 20 10])
    (add-scatter :connectgaps true)
    (add-scatter :y [15 25 nil 20 15 10 20 nil 25 15 15 20 30 25 15])
    (plot "line-gaps"  :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/48.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; @@
(-> (plotly)
    (plot-seq
      (for [[line-type y] (map vector
                               ["linear" "spline" "vhv" "hvh" "vh" "hv"]
                               (map #(m/add (* 5 %) [1 3 2 3 1]) (range 6)))]
        #(add-scatter % :y y :line {:shape line-type})))
    (set-layout :legend {:y 0.5 :traceorder "reversed"})
    (plot "Interpolation-with-Line-Plots" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/50.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; @@
 (let [rates [[74 82 80 74 73 72 74 70 70 66 66 69]
              [45 42 50 46 36 36 34 35 32 31 31 28]
              [13 14 20 24 20 24 24 40 35 41 43 50]
              [18 21 18 21 16 14 13 18 17 16 19 23]]
       labels ["Television" "Newspaper" "Internet" "Radio"]
       marker-sizes [8 8 12 8]
       colors ["rgba(67,67,67,1)" "rgba(115,115,115,1)" "rgba(49,130,189, 1)" "rgba(189,189,189,1)"]]
   (-> (plotly {:years [2001 2002 2003 2004 2005 2006 2007 2008 2009 2010 2011 2013]})
       (plot-seq
         (for [[rate color] (map vector rates colors)]
           #(add-scatter %
                         :x :years
                         :y rate
                         :mode "lines"
                         :line {:color color :width 2})))
       (plot-seq
         (for [[rate color marker-size] (map vector rates colors marker-sizes)]
           #(add-scatter %
                         :x [2001 2013]
                         :y [(first rate) (last rate)]
                         :mode "markers"
                         :marker {:color color :size marker-size})))
       (set-layout :xaxis {:showline true 
                           :showgrid false
                           :showticklabels true
                           :linecolor "rgb(204, 204, 204)"
                           :autotick false
                           :ticks  "outside"
                           :tickcolor "rgb(204, 204, 204)"
                           :tickwidth 2
                           :ticklen 5
                           :tickfont {:family "Arial" :size 12 :color "rgb(82, 82, 82)"}}
                   :yaxis {:showgrid false
                           :zeroline false
                           :showline false
                           :showticklabels false}
                   :showlegend false
                   :autosize false
                   :margin {:autoexpand false :l 100 :r 20 :t 110})
       (add-annotations 
         (for [[rate color label] (map vector rates colors labels)]
           {:xref "paper"
            :x 0.05
            :y (first rate)
            :xanchor "right"
            :yanchor "middle"
            :text (str label (first rate) "%")
            :font {:family "Arial" :size 16 :color color}
            :showarrow false}))
       
       (add-annotations 
         (for [[rate color label] (map vector rates colors labels)]
           {:xref "paper"
            :x 0.95
            :y (last rate)
            :xanchor "left"
            :yanchor "middle"
            :text (str (last rate) "%")
            :font {:family "Arial" :size 16 :color color}
            :showarrow false}))
       (add-annotations 
         {:xref "paper"
          :yref "paper"
          :x 0.5
          :y -0.1
          :xanchor "center"
          :yanchor "top"
          :text "Source: PewResearch Center & Storytelling with data"
          :font {:family "Arial" :size 12 :color "rgb(150,150,150)"}
          :showarrow false})
       (add-annotations
         {:xref "paper"
          :yref "paper"
          :x 0.0
          :y 1.05
          :xanchor "left"
          :yanchor "bottom"
          :text "Main Source for News"
          :font {:family "Arial" :size 30 :color "rgb(37, 37, 37)"}
          :showarrow false})
       (plot "Label-Lines-with-Annotations" :fileopt "overwrite")
   embed-url))
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/52.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; @@

;; @@
