(defproject plotly-clj "0.1.0-SNAPSHOT"
  :description "Plotly Clojure Client"
  :url "https://github.com/findmyway/plotly-clj"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"
            :year 2017
            :key "mit"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [hiccup "1.0.5"]
                 [org.clojure/data.json  "0.2.6"]
                 [net.mikera/core.matrix "0.57.0"]
                 [com.rpl/specter "0.13.2"] 
                 [http-kit  "2.2.0"]
                 [lein-gorilla "0.3.6"]
                 [org.clojure/data.csv "0.1.3"]]
  :plugins [[lein-gorilla "0.3.6"]])
