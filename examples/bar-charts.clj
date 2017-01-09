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
    iplot)
;; @@
;; =>
;;; {"type":"html","content":"<div class=\"plotly-graph-div\" id=\"4e2a0887-618c-4e07-97e6-977cb891feeb\"></div><script type=\"text/javascript\">window.PLOTLYENV=window.PLOTLYENV || {};window.PLOTLYENV.BASE_URL=\"https://plot.ly\";Plotly.newPlot(\"4e2a0887-618c-4e07-97e6-977cb891feeb\",[{\"type\":\"bar\",\"x\":[\"giraffes\",\"orangutans\",\"monkeys\"],\"y\":[20,14,23],\"name\":\"SF Zoo\"}],{},{})</script>","value":"pr'ed value"}
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
    iplot)
;; @@
;; =>
;;; {"type":"html","content":"<div class=\"plotly-graph-div\" id=\"856aa432-199a-4841-ac02-9f348b6b3b75\"></div><script type=\"text/javascript\">window.PLOTLYENV=window.PLOTLYENV || {};window.PLOTLYENV.BASE_URL=\"https://plot.ly\";Plotly.newPlot(\"856aa432-199a-4841-ac02-9f348b6b3b75\",[{\"type\":\"bar\",\"x\":[\"giraffes\",\"orangutans\",\"monkeys\"],\"y\":[20,14,23],\"name\":\"SF Zoo\"},{\"type\":\"bar\",\"x\":[\"giraffes\",\"orangutans\",\"monkeys\"],\"y\":[12,18,29],\"name\":\"LA Zoo\"}],{},{})</script>","value":"pr'ed value"}
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
    iplot)
;; @@
;; =>
;;; {"type":"html","content":"<div class=\"plotly-graph-div\" id=\"6e38eb62-ed0e-471c-a649-03b30b9cc02f\"></div><script type=\"text/javascript\">window.PLOTLYENV=window.PLOTLYENV || {};window.PLOTLYENV.BASE_URL=\"https://plot.ly\";Plotly.newPlot(\"6e38eb62-ed0e-471c-a649-03b30b9cc02f\",[{\"type\":\"bar\",\"x\":[\"giraffes\",\"orangutans\",\"monkeys\"],\"y\":[20,14,23],\"name\":\"SF Zoo\"},{\"type\":\"bar\",\"x\":[\"giraffes\",\"orangutans\",\"monkeys\"],\"y\":[12,18,29],\"name\":\"LA Zoo\"}],{\"barmode\":\"stack\"},{})</script>","value":"pr'ed value"}
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
    iplot)
;; @@
;; =>
;;; {"type":"html","content":"<div class=\"plotly-graph-div\" id=\"e02573bf-ea06-41e7-96a3-86d81cf941cd\"></div><script type=\"text/javascript\">window.PLOTLYENV=window.PLOTLYENV || {};window.PLOTLYENV.BASE_URL=\"https://plot.ly\";Plotly.newPlot(\"e02573bf-ea06-41e7-96a3-86d81cf941cd\",[{\"type\":\"bar\",\"y\":[20,14,23],\"marker\":{\"color\":\"rgb(158,202,225)\",\"line\":{\"color\":\"rgb(8,48,107)\",\"width\":1.5}},\"opacity\":0.5,\"x\":[\"Product A\",\"Product B\",\"Product C\"],\"text\":[\"27% market share\",\"24% market share\",\"19% market share\"]}],{\"title\":\"January 2013 Sales Report\"},{})</script>","value":"pr'ed value"}
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
    iplot))
;; @@
;; =>
;;; {"type":"html","content":"<div class=\"plotly-graph-div\" id=\"1dc38bf0-5d5a-4cd0-81b6-a49281f8568e\"></div><script type=\"text/javascript\">window.PLOTLYENV=window.PLOTLYENV || {};window.PLOTLYENV.BASE_URL=\"https://plot.ly\";Plotly.newPlot(\"1dc38bf0-5d5a-4cd0-81b6-a49281f8568e\",[{\"type\":\"bar\",\"y\":[20,14,23],\"marker\":{\"color\":\"rgb(158,202,225)\",\"line\":{\"color\":\"rgb(8,48,107)\",\"width\":1.5}},\"opacity\":0.5,\"x\":[\"Product A\",\"Product B\",\"Product C\"],\"text\":[\"27% market share\",\"24% market share\",\"19% market share\"]}],{\"title\":\"January 2013 Sales Report\",\"annotations\":[{\"x\":\"Product A\",\"y\":20,\"text\":20,\"showarrow\":false,\"yanchor\":\"bottom\"},{\"x\":\"Product B\",\"y\":14,\"text\":14,\"showarrow\":false,\"yanchor\":\"bottom\"},{\"x\":\"Product C\",\"y\":23,\"text\":23,\"showarrow\":false,\"yanchor\":\"bottom\"}]},{})</script>","value":"pr'ed value"}
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
    iplot)
;; @@
;; =>
;;; {"type":"html","content":"<div class=\"plotly-graph-div\" id=\"974ba933-b980-49dc-afd0-ca52048c7372\"></div><script type=\"text/javascript\">window.PLOTLYENV=window.PLOTLYENV || {};window.PLOTLYENV.BASE_URL=\"https://plot.ly\";Plotly.newPlot(\"974ba933-b980-49dc-afd0-ca52048c7372\",[{\"type\":\"bar\",\"x\":[\"Jan\",\"Feb\",\"Mar\",\"Apr\",\"May\",\"Jun\",\"Jul\",\"Aug\",\"Sep\",\"Oct\",\"Nov\",\"Dec\"],\"y\":[20,14,25,16,18,22,19,15,12,16,14,17],\"name\":\"Primary Product\",\"marker\":{\"color\":\"rgb(49,130,189)\"}},{\"type\":\"bar\",\"x\":[\"Jan\",\"Feb\",\"Mar\",\"Apr\",\"May\",\"Jun\",\"Jul\",\"Aug\",\"Sep\",\"Oct\",\"Nov\",\"Dec\"],\"y\":[19,14,22,14,16,19,15,14,10,12,12,16],\"name\":\"Secondary Product\",\"marker\":{\"color\":\"rgb(204,204,204)\"}}],{\"xaxis\":{\"tickangle\":-45}},{})</script>","value":"pr'ed value"}
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
    iplot)
;; @@
;; =>
;;; {"type":"html","content":"<div class=\"plotly-graph-div\" id=\"41599456-1686-4cbb-8722-e192491247d3\"></div><script type=\"text/javascript\">window.PLOTLYENV=window.PLOTLYENV || {};window.PLOTLYENV.BASE_URL=\"https://plot.ly\";Plotly.newPlot(\"41599456-1686-4cbb-8722-e192491247d3\",[{\"type\":\"bar\",\"y\":[20,14,23,25,22],\"marker\":{\"color\":[\"rgba(204,204,204,1)\",\"rgba(222,45,38,0.8)\",\"rgba(204,204,204,1)\",\"rgba(204,204,204,1)\",\"rgba(204,204,204,1)\"]},\"x\":[\"Feature A\",\"Feature B\",\"Feature C\",\"Feature D\",\"Feature E\"]}],{},{})</script>","value":"pr'ed value"}
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
    iplot)
;; @@
;; =>
;;; {"type":"html","content":"<div class=\"plotly-graph-div\" id=\"167b774c-a2b2-44b4-b235-d76ee3f5d739\"></div><script type=\"text/javascript\">window.PLOTLYENV=window.PLOTLYENV || {};window.PLOTLYENV.BASE_URL=\"https://plot.ly\";Plotly.newPlot(\"167b774c-a2b2-44b4-b235-d76ee3f5d739\",[{\"type\":\"bar\",\"x\":[1995,1996,1997,1998,1999,2000,2001,2002,2003,2004,2005,2006,2007,2008,2009,2010,2011,2012],\"y\":[219,146,112,127,124,180,236,207,236,263,350,430,474,526,488,537,500,439],\"name\":\"Rest of world\",\"marker\":{\"color\":\"rgb(55, 83, 109)\"}},{\"type\":\"bar\",\"x\":[1995,1996,1997,1998,1999,2000,2001,2002,2003,2004,2005,2006,2007,2008,2009,2010,2011,2012],\"y\":[16,13,10,11,28,37,43,55,56,88,105,156,270,299,340,403,549,499],\"name\":\"China\",\"marker\":{\"color\":\"rgb(26, 118, 255)\"}}],{\"legend\":{\"x\":0,\"y\":1.0},\"xaxis\":{\"tickfont\":{\"size\":14,\"color\":\"rgb(107, 107, 107)\"}},\"bargap\":0.15,\"title\":\"US Export of Plastic Scrap\",\"yaxis\":{\"title\":\"USD (millions)\",\"titlefont\":{\"size\":14,\"color\":\"rgb(107, 107, 107)\"},\"tickfont\":{\"size\":14,\"color\":\"rgb(107, 107, 107)\"}},\"bargroupgap\":0.1},{})</script>","value":"pr'ed value"}
;; <=

;; **
;;; ##Waterfall Bar Chart
;; **

;; @@
(first (d/dataset {:xs ["Product<br>Revenue" "Services<br>Revenue" 
                          "Total<br>Revenue" "Fixed<br>Costs"
                          "Variable<br>Costs" "Total<br>Costs" "Total"]
                     :ys  [400 660 660 590 400 400 340]
                     :text ["$430K" "$260K" "$690K" "$-120K" "$-200K" "$-320K" "$370K"]}))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-string'>&quot;Product&lt;br&gt;Revenue&quot;</span>","value":"\"Product<br>Revenue\""},{"type":"html","content":"<span class='clj-long'>400</span>","value":"400"},{"type":"html","content":"<span class='clj-string'>&quot;$430K&quot;</span>","value":"\"$430K\""}],"value":"#dataset/row {:column-names [:xs :ys :text], :values [\"Product<br>Revenue\" 400 \"$430K\"]}"}
;; <=

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
   iplot))
;; @@
;; =>
;;; {"type":"html","content":"<div class=\"plotly-graph-div\" id=\"f44608a5-4586-467a-9e09-4378d0b2a7d8\"></div><script type=\"text/javascript\">window.PLOTLYENV=window.PLOTLYENV || {};window.PLOTLYENV.BASE_URL=\"https://plot.ly\";Plotly.newPlot(\"f44608a5-4586-467a-9e09-4378d0b2a7d8\",[{\"type\":\"bar\",\"x\":[\"Product<br>Revenue\",\"Services<br>Revenue\",\"Total<br>Revenue\",\"Fixed<br>Costs\",\"Variable<br>Costs\",\"Total<br>Costs\",\"Total\"],\"y\":[0,430,0,570,370,370,0],\"marker\":{\"color\":\"rgba(1,1,1, 0.0)\"}},{\"type\":\"bar\",\"x\":[\"Product<br>Revenue\",\"Services<br>Revenue\",\"Total<br>Revenue\",\"Fixed<br>Costs\",\"Variable<br>Costs\",\"Total<br>Costs\",\"Total\"],\"y\":[430,260,690,0,0,0,0],\"marker\":{\"color\":\"rgba(55, 128, 191, 0.7)\",\"line\":{\"color\":\"rgba(55, 128, 191, 1.0)\",\"width\":2}}},{\"type\":\"bar\",\"x\":[\"Product<br>Revenue\",\"Services<br>Revenue\",\"Total<br>Revenue\",\"Fixed<br>Costs\",\"Variable<br>Costs\",\"Total<br>Costs\",\"Total\"],\"y\":[0,0,0,120,200,320,0],\"marker\":{\"color\":\"rgba(219, 64, 82, 0.7)\",\"line\":{\"color\":\"rgba(219, 64, 82, 1.0)\",\"width\":2}}},{\"type\":\"bar\",\"x\":[\"Product<br>Revenue\",\"Services<br>Revenue\",\"Total<br>Revenue\",\"Fixed<br>Costs\",\"Variable<br>Costs\",\"Total<br>Costs\",\"Total\"],\"y\":[0,0,0,0,0,0,370],\"marker\":{\"color\":\"rgba(50, 171, 96, 0.7)\",\"line\":{\"color\":\"rgba(50, 171, 96, 1.0)\",\"width\":2}}}],{\"plot_bgcolor\":\"rgba(245, 246, 249, 1)\",\"paper_bgcolor\":\"rgba(245, 246, 249, 1)\",\"title\":\"Annual Profit- 2015\",\"showlegend\":false,\"barmode\":\"stack\",\"annotations\":[{\"x\":\"Product<br>Revenue\",\"y\":400,\"text\":\"$430K\",\"showarrow\":false,\"font\":{\"family\":\"Arial\",\"size\":14,\"color\":\"rgba(245, 246, 249, 1)\"}},{\"x\":\"Services<br>Revenue\",\"y\":660,\"text\":\"$260K\",\"showarrow\":false,\"font\":{\"family\":\"Arial\",\"size\":14,\"color\":\"rgba(245, 246, 249, 1)\"}},{\"x\":\"Total<br>Revenue\",\"y\":660,\"text\":\"$690K\",\"showarrow\":false,\"font\":{\"family\":\"Arial\",\"size\":14,\"color\":\"rgba(245, 246, 249, 1)\"}},{\"x\":\"Fixed<br>Costs\",\"y\":590,\"text\":\"$-120K\",\"showarrow\":false,\"font\":{\"family\":\"Arial\",\"size\":14,\"color\":\"rgba(245, 246, 249, 1)\"}},{\"x\":\"Variable<br>Costs\",\"y\":400,\"text\":\"$-200K\",\"showarrow\":false,\"font\":{\"family\":\"Arial\",\"size\":14,\"color\":\"rgba(245, 246, 249, 1)\"}},{\"x\":\"Total<br>Costs\",\"y\":400,\"text\":\"$-320K\",\"showarrow\":false,\"font\":{\"family\":\"Arial\",\"size\":14,\"color\":\"rgba(245, 246, 249, 1)\"}},{\"x\":\"Total\",\"y\":340,\"text\":\"$370K\",\"showarrow\":false,\"font\":{\"family\":\"Arial\",\"size\":14,\"color\":\"rgba(245, 246, 249, 1)\"}}]},{})</script>","value":"pr'ed value"}
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
      iplot))
;; @@
;; =>
;;; {"type":"html","content":"<div class=\"plotly-graph-div\" id=\"ad1844a0-5e10-41fd-a9aa-4748d497f967\"></div><script type=\"text/javascript\">window.PLOTLYENV=window.PLOTLYENV || {};window.PLOTLYENV.BASE_URL=\"https://plot.ly\";Plotly.newPlot(\"ad1844a0-5e10-41fd-a9aa-4748d497f967\",[{\"type\":\"bar\",\"y\":[1,4,9,16],\"x\":[1,2,3,4]},{\"type\":\"bar\",\"y\":[6,-8,-4.5,8],\"x\":[1,2,3,4]},{\"type\":\"bar\",\"y\":[-15,-3,4.5,-8],\"x\":[1,2,3,4]},{\"type\":\"bar\",\"y\":[-1,3,-3,-4],\"x\":[1,2,3,4]}],{\"barmode\":\"relative\"},{})</script>","value":"pr'ed value"}
;; <=

;; @@

;; @@
