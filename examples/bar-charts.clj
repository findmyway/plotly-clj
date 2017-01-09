;; gorilla-repl.fileformat = 1

;; **
;;; # Bar Charts
;; **

;; @@
(ns bar
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
(-> (plotly {:x ["giraffes" "orangutans" "monkeys"]})
    (add-bar
      :x :x
      :y [20 14 23]
      :name "SF Zoo")
    (plot "Basic-Bar-Chart"  :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/54.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; @@
(-> (plotly {:x ["giraffes" "orangutans" "monkeys"]})
    (add-bar
      :x :x
      :y [20 14 23]
      :name "SF Zoo")
    (add-bar
      :x :x
      :y [12 18 29]
      :name "LA Zoo")
    (plot "Grouped-Bar-Chart"  :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/56.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; @@
(-> (plotly {:x ["giraffes" "orangutans" "monkeys"]})
    (add-bar
      :x :x
      :y [20 14 23]
      :name "SF Zoo")
    (add-bar
      :x :x
      :y [12 18 29]
      :name "LA Zoo")
    (set-layout :barmode "stack")
    (plot "Stacked-Bar-Chart" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/58.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; **
;;; ##Bar Chart with Hover Text 
;; **

;; @@
(-> (plotly)
    (add-bar
      :x ["Product A" "Product B" "Product C"]
      :y [20 14 23]
      :text ["27% market share" "24% market share" "19% market share"]
      :marker {:color "rgb(158,202,225)"
               :line {:color "rgb(8,48,107)" :width 1.5}}
      :opacity 0.5)
    (set-layout :title "January 2013 Sales Report")
    (plot "Bar-Chart-with-Hover-Text" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/60.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; **
;;; ## Styled Bar Chart with Direct Labels
;; **

;; @@
(let [xs ["Product A" "Product B" "Product C"]
      ys [20 14 23]]
  (-> (plotly)
    (add-bar
      :x xs
      :y ys
      :text ["27% market share" "24% market share" "19% market share"]
      :marker {:color "rgb(158,202,225)"
               :line {:color "rgb(8,48,107)" :width 1.5}}
      :opacity 0.5)
    (set-layout :title "January 2013 Sales Report")
    (add-annotations 
      (for [[x y] (map vector xs ys)]
        {:x x :y y :text y :showarrow false :yanchor "bottom"}))
    (plot "Styled-Bar-Chart-with-Direct-Labels" :fileopt "overwrite")
    embed-url))
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/62.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; **
;;; ## Rotated Bar Chart Labels
;; **

;; @@
(-> (plotly {:x ["Jan" "Feb" "Mar" "Apr" "May" "Jun""Jul" "Aug" "Sep" "Oct" "Nov" "Dec"]})
    (add-bar 
      :x :x
      :y [20 14 25 16 18 22 19 15 12 16 14 17]
      :name "Primary Product"
      :marker {:color "rgb(49,130,189)"})
    (add-bar
      :x :x
      :y [19 14 22 14 16 19 15 14 10 12 12 16]
      :name "Secondary Product"
      :marker {:color "rgb(204,204,204)"})
    (set-layout :xaxis {:tickangle -45})
    (plot "Rotated Bar Chart Labels" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/64.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; **
;;; ##Customizing Individual Bar Colors
;; **

;; @@
(-> (plotly)
    (add-bar 
      :x ["Feature A" "Feature B" "Feature C""Feature D" "Feature E"]
      :y [20 14 23 25 22]
      :marker {:color ["rgba(204,204,204,1)" "rgba(222,45,38,0.8)" 
                       "rgba(204,204,204,1)" "rgba(204,204,204,1)" "rgba(204,204,204,1)"]})
    (plot "Customizing-Individual-Bar-Colors" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/66.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; **
;;; ##Colored and Styled Bar Chart
;; **

;; @@
(-> (plotly {:x [1995 1996 1997 1998 1999 2000 2001 2002 2003 2004 2005 2006 2007 2008 2009 2010 2011 2012]})
    (add-bar
      :x :x
      :y [219 146 112 127 124 180 236 207 236 263 350 430 474 526 488 537 500 439]
      :name "Rest of world"
      :marker {:color "rgb(55, 83, 109)"})
    (add-bar
      :x :x
      :y [16 13 10 11 28 37 43 55 56 88 105 156 270 299 340 403 549 499]
      :name "China"
      :marker {:color "rgb(26, 118, 255)"})
    (set-layout
      :title "US Export of Plastic Scrap"
      :xaxis {:tickfont {:size 14 :color "rgb(107, 107, 107)"}}
      :yaxis {:title "USD (millions)" 
              :titlefont {:size 14 :color "rgb(107, 107, 107)"}
              :tickfont {:size 14 :color "rgb(107, 107, 107)"}}
      :legend {:x 0
               :y 1.0}
      :bargap 0.15
      :bargroupgap 0.1)
    (plot "Colored-and-Styled-Bar-Chart" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/68.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; **
;;; ##Waterfall Bar Chart
;; **

;; @@

(let [ds (d/dataset {:xs ["Product<br>Revenue" "Services<br>Revenue" 
                          "Total<br>Revenue" "Fixed<br>Costs"
                          "Variable<br>Costs" "Total<br>Costs" "Total"]
                     :ys  [400 660 660 590 400 400 340]
                     :text ["$430K" "$260K" "$690K" "$-120K" "$-200K" "$-320K" "$370K"]})]
  (->(plotly ds)
   (add-bar :x :xs
            :y [0 430 0 570 370 370 0]
            :marker {:color "rgba(1,1,1, 0.0)"})
   (add-bar :x :xs
            :y [430 260 690 0 0 0 0]
            :marker {:color "rgba(55, 128, 191, 0.7)"
                     :line {:color "rgba(55, 128, 191, 1.0)" :width 2}})
   (add-bar :x :xs
            :y [0 0 0 120 200 320 0]
            :marker {:color "rgba(219, 64, 82, 0.7)"
                     :line {:color "rgba(219, 64, 82, 1.0)" :width 2}})
   (add-bar :x :xs
            :y [0 0 0 0 0 0 370]
            :marker {:color "rgba(50, 171, 96, 0.7)"
                     :line {:color "rgba(50, 171, 96, 1.0)" :width 2}})
   (set-layout :title "Annual Profit- 2015"
               :barmode "stack"
               :showlegend false 
               :paper_bgcolor "rgba(245, 246, 249, 1)"
               :plot_bgcolor "rgba(245, 246, 249, 1)")
   (add-annotations
     (for [[x y t] ds]
       {:x x :y y :text t :showarrow false
        :font {:family "Arial" :size 14 :color "rgba(245, 246, 249, 1)"}}))
   (plot "Waterfall Bar Chart" :fileopt "overwrite")
   embed-url))
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/70.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; **
;;; ##Bar Chart with Relative Barmode
;;; 
;; **

;; @@
(let [x [1 2 3 4]]
  (-> (plotly)
      (add-bar :x x :y [1 4 9 16])
      (add-bar :x x :y [6 -8 -4.5 8])
      (add-bar :x x :y [-15 -3 4.5 -8])
      (add-bar :x x :y [-1 3 -3 -4])
      (set-layout :barmode "relative")
      (plot "Bar Chart with Relative Barmode" :fileopt "overwrite")
      embed-url))
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/72.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; @@

;; @@
