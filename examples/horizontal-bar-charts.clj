;; gorilla-repl.fileformat = 1

;; **
;;; #Horizontal-Bar-Charts
;; **

;; @@
(ns horizontal-bar
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
(-> (plotly)
    (add-bar :x [20 14 23]
             :y ["giraffes" "orangutans" "monkeys"]
             :orientation "h")
    (plot "Basic Horizontal Bar Chart" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/74.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; **
;;; ##Colored Horizontal Bar Chart
;; **

;; @@
(-> (plotly)
    (add-bar :x [20 14 23]
             :y ["giraffes" "orangutans" "monkeys"]
             :name "SF Zoo"
             :orientation "h"
             :marker {:color "rgba(246, 78, 139, 0.6)" :line {:color "rgba(246, 78, 139, 1.0)" :width 3}})
    (add-bar :x [12 18 19]
             :y  ["giraffes" "orangutans" "monkeys"]
             :name "LA Zoo"
             :orientation "h"
             :marker {:color "rgba(58, 71, 80, 0.6)" :line {:color "rgba(58, 71, 80, 1.0)" :width 3}})
    (set-layout :barmode "stack")
    (plot "Colored Horizontal Bar Chart" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/76.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; **
;;; ##Color Palette for Bar Chart
;; **

;; @@
(let [top-labels ["Strongly<br>agree" "Agree" "Neutral" "Disagree" "Strongly<br>disagree"]
      colors ["rgba(38 24 74 0.8)" "rgba(71 58 131 0.8)" "rgba(122 120 168 0.8)"
                              "rgba(164 163 204 0.85)" "rgba(190 192 213 1)"]
      x-data [[21 30 21 16 12] [24 31 19 15 11] [27 26 23 11 13] [29 24 15 18 14]]
      y-data ["The course was effectively<br>organized"
              "The course developed my<br>abilities and skills for<br>the subject"
              "The course developed my<br>ability to think critically about<br>the subject"
              "I would recommend this<br>course to a friend"]
      get-mid (fn [xs] 
                (let [s (butlast (reductions + 0 xs))
                      halves (m/div xs 2)]
                  (m/add s halves)))
      xs-mid (map get-mid x-data)]
  (-> (plotly)
      (plot-seq
        (for [[x c] (map vector (m/transpose x-data) colors)]
          #(add-bar %
                    :x x 
                    :y y-data
                    :orientation "h" 
                    :marker {:color c
                             :line {:color "rgb(248, 248, 249)" :width 1}})))
      (set-layout :xaxis {:showgrid false
                          :showline false
                          :showticklabels false
                          :zeroline false
                          :domain [0.15 1]}
                  :yaxis {:showgrid false
                          :showline false
                          :showticklabels false
                          :zeroline false}
                  :barmode "stack"
                  :showlegend false
                  :paper_bgcolor "rgb(248, 248, 255)"
                  :plot_bgcolor "rgb(248, 248, 255)"
                  :margin {:l 120 :r 10 :t 120 :b 80})
      (add-annotations 
        (for [[x y] (map vector x-data y-data)]
          {:xref "paper" :yref "y" :x 0.14 :y y :xanchor "right" :text y :showarrow false :align "right"}))
      (add-annotations
        (for [idy (range (count y-data)) idx (range (count (first xs-mid)))]
            {:xref "x" 
             :yref "y"
             :x (nth (nth xs-mid idy) idx)
             :y (nth y-data idy) 
             :text (str (nth (nth x-data idy) idx) "%")
             :showarrow false
             :font {:color "rgb(248, 248, 255)"
                    :family "Arial"
                    :size 14}}))
      (add-annotations
        (for [[x label] (map vector (last xs-mid) top-labels)]
          {:xref "x" :yref "paper"
           :x x :y 1.1 :text label :showarrow false}))
      
      (plot "Color Palette for Bar Chart" :fileopt "overwrite")
      embed-url))
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/78.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; @@
(let [y-saving [1.3586 2.2623 4.9822 6.5097 7.4812 7.5133 15.2148 17.5205]
      y-net-worth [93453.92 81666.57 69889.62 78381.53 141395.3 92969.02 66090.18 122379.3]
      x-saving ["Japan" "United Kingdom" "Canada" "Netherlands"
                "United States" "Belgium" "Sweden" "Switzerland"]
      x-net-worth ["Japan" "United Kingdom" "Canada" "Netherlands" 
                     "United States" "Belgium" "Sweden" "Switzerland"]]
  (-> (plotly)
      (add-bar :x y-saving
               :y x-saving
               :xaxis "x1"
               :yaxis "y1"
               :name "Household savings, percentage of household disposable income"
               :orientation "h"
               :marker {:color "rgba(50, 171, 96, 0.6)" 
                        :line {:color "rgba(50, 171, 96, 1.0)" :width 1}})
      (add-scatter :x y-net-worth
                   :xaxis "x2"
                   :yaxis "y2"
                   :y x-saving
                   :mode "lines+markers"
                   :name "Household net worth, Million USD/capita"
                   :line {:color "rgb(128, 0, 128)"})
      (set-layout :title "Household savings & net worth for eight OECD countries"
                  :yaxis1 {:showticklabels true 
                           :domain [0 0.85]}
                  :yaxis2 {:anchor "x2" 
                           :linecolor "rgba(102, 102, 102, 0.8)"
                           :domain [0 0.85] 
                           :showticklabels false}
                  :xaxis1 {:domain [0 0.45] 
                           :zeroline false 
                           :showline false 
                           :showticklabels true
                           :showgrid true}
                  :xaxis2 {:domain [0.47 1] 
                           :zeroline false 
                           :showline false 
                           :showticklabels true
                           :showgrid true 
                           :dtick 25000 
                           :side "top"}
                  :margin {:l 100 :r 20 :t 70 :b 70}
                  :legend {:x 0.029 :y 1.05 :font {:size 10}})
      (plot "Bar Chart with Line Plot" :fileopt "overwrite")
      embed-url))
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/80.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; @@

;; @@
