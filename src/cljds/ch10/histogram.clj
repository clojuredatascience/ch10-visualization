(ns cljds.ch10.histogram
  (:require [cljds.ch10.data :refer :all]
            [quil.core :as q]
            [quil.middleware :as m]
            [incanter.core :as i]
            [incanter.stats :as s]))

(defn bin [n-bins xs]
  (let [min-x    (apply min xs)
        range-x  (- (apply max xs) min-x)
        max-bin  (dec n-bins)
        bin-fn   (fn [x]
                   (-> (- x min-x)
                       (/ range-x)
                       (* n-bins)
                       (int)
                       (min max-bin)))]
    (map bin-fn xs)))

(defn ratio->grayscale [f m sd]
  (-> (- f m)
      (/ sd)
      (q/map-range -1 1 0 255)
      (min 255)
      (max 0)
      (q/color)))

(defn z-score->heat [z-score]
  (let [colors [(q/color 0 0 255)   ;; Blue
                (q/color 0 255 255) ;; Turquoise
                (q/color 0 255 0)   ;; Green
                (q/color 255 255 0) ;; Yellow
                (q/color 255 0 0)]  ;; Red
        offset  (-> (q/map-range z-score -1 3 0 3.999)
                    (max 0)
                    (min 3.999))]
    (q/lerp-color (nth colors offset)
                  (nth colors (inc offset))
                  (rem offset 1))))

(defn histogram-2d [xs ys n-bins]
  (-> (map vector
           (bin n-bins xs)
           (bin n-bins ys))
      (frequencies)))

(defn draw-grid [{:keys [n-bins size]}]
  (let [[width height] size
        x-scale (/ width n-bins)
        y-scale (/ height n-bins)
        setup (fn []
                (doseq [x (range n-bins)
                        y (range n-bins)
                        :let [x-pos (* x x-scale)
                              y-pos (- height
                                       (* (inc y) y-scale))]]
                  (q/rect x-pos y-pos x-scale y-scale)))]
    (q/sketch :setup setup :size size)))

(defn draw-filled-grid [{:keys [n-bins size fill-fn]}]
  (let [[width height] size
        x-scale (/ width n-bins)
        y-scale (/ height n-bins)
        setup (fn []
                (doseq [x (range n-bins)
                        y (range n-bins)
                        :let [x-pos (* x x-scale)
                              y-pos (- height
                                       (* (inc y) y-scale))]]
                  (q/fill (fill-fn x y))
                  (q/rect x-pos y-pos x-scale y-scale)))]
    (q/sketch :setup setup :size size)))
