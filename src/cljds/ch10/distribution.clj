(ns ch10.distribution
  (:require [incanter.optimize :as o]
            [incanter.charts :as c]
            [incanter.core :as i]
            [incanter.stats :as s]
            [incanter.datasets :as d]))

(def actuals
  {1 0.42
   4 0.3
   5 0.13
   10 0.11
   80 0.05})

(def actuals
  {1 0.42
   3 0.3
   7.5 0.13
   15 0.11})

(defn chart2 []
  (-> (c/scatter-plot (range (count (points))) (points))
      (i/view)))

(defn polynomial-fn [coefs degree]
  (fn [x]
    (first
     (i/mmult (i/trans coefs)
              [(i/pow x 1) (i/pow x 8)]))))
(defn chart []
  (let [x (range (count (points)))
        degree 10
        xs (reduce i/bind-columns
                   [(i/pow x 1) (i/pow x 10)])
        model (s/linear-model (points) xs :intercept false)]
    (-> (c/scatter-plot (range (count (points))) (points))
        (c/add-function (polynomial-fn (:coefs model) degree) 0 100)
        (i/view))))

#_(i/with-data (d/get-dataset :co2)
    i/view (c/area-chart :Type :uptake
                     :title "CO2 Uptake"
                     :group-by :Treatment
                     :x-label "Grass Types" :y-label "Uptake"
                     :legend true))
