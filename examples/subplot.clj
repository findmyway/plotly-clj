;; gorilla-repl.fileformat = 1

;; **
;;; # Subplots
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

;; @@
(online-init)
;; @@
;; =>
;;; {"type":"html","content":"<script src=\"https://cdn.plot.ly/plotly-latest.min.js\" type=\"text/javascript\"></script>","value":"pr'ed value"}
;; <=

;; @@
(-> (plotly)
    (add-scatter :x [1 2 3] :y [2 3 4])
    (add-scatter :x [20 30 40] :y [50 60 70])
    (subplot :nrow 1 :ncol 2)
    (plot "simple subplots 1" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/84.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; @@
(-> (plotly)
    (add-scatter :x [1 2 3] :y [2 3 4])
    (add-scatter :x [20 30 40] :y [5 5 5])
    (add-scatter :x [2 3 4] :y [600 700 800])
    (add-scatter :x [4000 5000 6000] :y [7000 8000 9000])
    (subplot :nrow 2 :ncol 2)
    (plot "simple subplots 2" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/86.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; @@
(-> (plotly)
    (plot-seq
      (for [_ (range 10)] 
        #(add-scatter % :x [1 2 3] :y [2 3 4])))
    (subplot :nrow 2 :ncol 5 :sharex true)
    (plot "simple subplots 3" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/88.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; @@
(-> (plotly)
    (add-scatter :x [1 2 3] :y [2 3 4])
    (add-scatter-3d :x [1 2 3] :y [2 3 4] :z [1 1 1])
    (add-scatter :x [1 2 3] :y [2 3 4])
    (add-scatter-3d :x [1 2 3] :y [2 3 4] :z [1 1 1])
    (subplot :nrow 2)
    (plot "simple subplots 4" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/130.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; @@

;; @@
