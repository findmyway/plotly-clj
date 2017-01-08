(ns plotly-clj.scale
  (:require [clojure.core.matrix :as m]
            [clojure.core.matrix.dataset :as d]))

(defn scale-size
  "TODO:
  Check every? number? 
  0 <= min-size <= max-size"
  ([xs]
   (scale-size xs 10 50))
  ([xs min-size max-size]
   (let [min-x (m/emin xs)
         max-x (m/emax xs)]
     (if (= max-x min-x)
       (m/add (m/zero-array (m/shape xs)) min-size)
       (m/add min-size
              (m/mul (- max-size min-size)
                     (m/div (m/add xs (- min-x))
                            (- max-x min-x))))))))

(defn factor
  [xs]
  (if (every? number? xs)
    xs
    (let [val-idx (into {} (map-indexed #(vector %2 %1) (set xs)))]
      (mapv val-idx xs))))

(defn scale-color
  [xs]
  (if (every? number? xs)
    xs
    (let [val-idx (into {} (map-indexed #(vector %2 %1) (set xs)))
          interval (float (/ 360 (count val-idx)))]
      (map #(str "hsl(" (* (val-idx %) interval) ",0.5,0.5)") xs))))
