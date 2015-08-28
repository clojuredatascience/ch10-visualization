(defproject cljds/ch10 "0.1.0"
  :description "Example code for the book Clojure for Data Science"
  :url "https://github.com/clojuredatascience/ch10-visualization"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-3126"]
                 [org.clojure/tools.cli "0.3.1"]
                 [quil "2.2.5" :exclusions [org.clojure/clojure]]
                 [incanter "1.5.5"]]
  :resource-paths ["data"]
  :repl-options {:init-ns cljds.ch10.examples}
  :main cljds.ch10.core
  :aot [cljds.ch10.core]
  :jvm-opts ["-Xmx4G"])
