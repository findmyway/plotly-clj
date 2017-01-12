;; gorilla-repl.fileformat = 1

;; **
;;; #Surface
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
(def data 
  (let [in-file (slurp "https://raw.githubusercontent.com/plotly/datasets/master/api_docs/mt_bruno_elevation.csv")
        data (doall (csv/read-csv in-file))]
    (m/emap #(Float/parseFloat %) (d/dataset (rest data)))
  ))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;scatter/data</span>","value":"#'scatter/data"}
;; <=

;; @@
(-> (plotly)
    (add-surface :z (m/to-nested-vectors data))
    (plot "3D surface" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/94.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; @@
(-> (plotly)
    (add-surface :z [[0 0 0]
                     [0 1 0]
                     [0 0 0]
                     [0 -1 0]
                     [0 0 0]])
    (plot "3D surface 2" :fileopt "overwrite")
    embed-url)
;; @@
;; =>
;;; {"type":"html","content":"<iframe height=\"600\" src=\"//plot.ly/~findmyway/96.embed\" width=\"800\"></iframe>","value":"pr'ed value"}
;; <=

;; @@

;; @@
