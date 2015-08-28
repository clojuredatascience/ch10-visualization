(ns cljds.ch10.examples
  (:require [cljds.ch10.data :refer :all]
            [cljds.ch10.histogram :refer :all]
            [cljds.ch10.wealth :refer :all]
            [clojure.string :as str]
            [incanter.svg :as svg]
            [incanter.stats :as s]
            [incanter.charts :as c]
            [incanter.core :as i]
            [quil.core :as q]
            [quil.middleware :as m]))

(defn ex-10-1 []
  (bin 5 (range 20)))

(defn ex-10-2 []
  (frequencies (bin 5 (range 20))))

(defn ex-10-3 []
  (histogram-2d (range 20)
                (reverse (range 20)) 5))

(defn ex-10-4 []
  (let [data (load-data :ru-victors)]
    (histogram-2d (i/$ :turnout data)
                  (i/$ :victors-share data) 5)))

(defn ex-10-5 []
  (let [setup #(q/rect 0 0 50 50)]
    (q/sketch :setup setup
              :size [250 250])))

(defn ex-10-6 []
  (let [data (load-data :ru-victors)
        n-bins 5
        hist (histogram-2d (i/$ :turnout data)
                           (i/$ :victors-share data)
                           n-bins)
        max-val (apply max (vals hist))
        fill-fn (fn [x y]
                  (-> (get hist [x y] 0)
                      (/ max-val)
                      (* 255)))]
    (draw-filled-grid {:n-bins n-bins
                       :size [250 250]
                       :fill-fn fill-fn})))

(defn ex-10-7 []
  (let [data (load-data :ru-victors)
        n-bins 25
        hist (histogram-2d (i/$ :turnout data)
                           (i/$ :victors-share data)
                           n-bins)
        max-val (apply max (vals hist))
        fill-fn (fn [x y]
                  (-> (get hist [x y] 0)
                      (/ max-val)
                      (* 255)))]
    (draw-filled-grid {:n-bins n-bins
                       :size [250 250]
                       :fill-fn fill-fn})))

(defn ex-10-8 []
  (let [data (load-data :ru-victors)
        n-bins 25
        hist (histogram-2d (i/$ :turnout data)
                           (i/$ :victors-share data)
                           n-bins)
        mean (s/mean (vals hist))
        sd   (s/sd   (vals hist))
        fill-fn (fn [x y]
                  (-> (get hist [x y] 0)
                      (- mean)
                      (/ sd)
                      (q/map-range -1 3 0 255)))]
    (draw-filled-grid {:n-bins n-bins
                       :size [250 250]
                       :fill-fn fill-fn})))

(defn ex-10-9 []
  (let [data (load-data :ru-victors)
        n-bins 25
        hist (histogram-2d (i/$ :turnout data)
                           (i/$ :victors-share data)
                           n-bins)
        mean (s/mean (vals hist))
        sd   (s/sd   (vals hist))
        fill-fn (fn [x y]
                  (-> (get hist [x y] 0)
                      (- mean)
                      (/ sd)
                      (z-score->heat)))]
    (draw-filled-grid {:n-bins n-bins
                       :size [250 250]
                       :fill-fn fill-fn})))

(defn ex-10-10 []
  (let [categories ["0-79" "80-89" "90-95" "96-99" "100"]
        percentage [5      11      13      30      42   ]]
    (-> (c/bar-chart categories percentage
                     :x-label "Category"
                     :y-label "% Financial Wealth")
        (i/view))))

(defn ex-10-11 []
  (let [categories (range (count wealth-distribution))]
    (-> (c/bar-chart categories wealth-distribution
                     :x-label "Percentile"
                     :y-label "% Financial Wealth")
        (i/view))))

(defn ex-10-12 []
  (let [size [960 540]]
    (q/sketch :size size
              :setup draw-bars)))

(defn ex-10-13 []
  (let [size [960 540]]
    (q/sketch :size size
              :setup (fn []
                       (draw-bars)
                       (draw-axis-labels)
                       (draw-title)))))

(defn ex-10-14 []
  (let [size [960 540]]
    (q/sketch :size size
              :setup (fn []
                       (draw-shapes)
                       (draw-bars)
                       (draw-axis-labels)
                       (draw-title)))))

(defn ex-10-15 []
  (let [size [960 540]]
    (q/sketch :size size
              :setup (fn []
                       (draw-shapes)
                       (draw-banknotes)
                       (draw-axis-labels)
                       (draw-title)))))

(defn ex-10-16 []
  (let [expected [3 6.5 12 20 58.5]
        width  640
        height 480
        setup (fn []
                (q/background 255)
                (plot-area expected 0 height width height))]
    (q/sketch :setup setup :size [width height])))

(defn ex-10-17 []
  (let [expected [3 6.5 12 20 58.5]
        width  640
        height 480
        setup (fn []
                (q/background 255)
                (plot-full-area expected 0 height width height))]
    (q/sketch :setup setup :size [width height])))

(defn ex-10-18 []
  (let [expected [3 6.5 12 20 58.5]
        width  640
        height 480
        setup (fn []
                (q/background 255)
                (plot-smooth-area expected 0 height
                                  width height))]
    (q/sketch :setup setup :size [width height])))

(defn ex-10-19 []
  (let [expected [3 6.5 12 20 58.5]
        ideal    [10.5 14 21.5 22 32]
        width  640
        height 480
        setup (fn []
                (q/background 100)
                (plot-areas [expected ideal] 0 height
                            width height))]
    (q/sketch :setup setup :size [width height])))

(defn ex-10-20 []
  (let [size [960 540]]
    (q/sketch :size size
              :setup (fn []
                       (draw-shapes)
                       (draw-expected-ideal)
                       (draw-banknotes)
                       (draw-axis-labels)
                       (draw-title)))))

(defn ex-10-21 []
  (let [size [960 540]]
    (q/sketch :size size
              :setup (fn []
                       (draw-shapes)
                       (draw-expected-ideal)
                       (draw-banknotes)
                       (draw-axis-labels)
                       (draw-title))
              :renderer :pdf
              :output-file "wealth-distribution.pdf")))
