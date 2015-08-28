(ns cljds.ch10.wealth
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [clojure.string :as s]))

(def plot-x 56)
(def plot-y 60)
(def plot-width 757)
(def plot-height 400)
(def bar-width 7)
(def pc1-label-x 840)
(def pc1-label-y (+ plot-height plot-y 32))
(def label-y 520)

(def title-x 480)
(def title-y 220)

(def icons-y 480)
(def icon-width 6)
(def icon-height 15)

(def wealth-distribution
  (concat (repeat 80 (/ 5  80))
          (repeat 10 (/ 11 10))
          (repeat 5  (/ 13 5))
          (repeat 4  (/ 30 4))
          (repeat 1  (/ 42 1))))

(defn group-offset [i]
  (* (quot i 10) 7))

(defn draw-pile [x y height]
  (q/no-stroke)
  (q/fill 80 127 64)
  (doseq [y (range (* 3 (quot (- y height) 3)) y 3)]
    (q/rect x y 6 2)))

(defn banknotes [x y width height]
  (q/no-stroke)
  (q/fill 80 127 64)
  (doseq [y (range (* 3 (quot y 3)) (+ y height) 3)
          x (range x (+ x width) 7)]
    (q/rect x y 6 2)))

(defn draw-banknotes []
  (let [pc99    (vec (butlast wealth-distribution))
        pc1     (last wealth-distribution)
        y-max   (apply max pc99)
        y-scale (fn [x] (* (/ x y-max) plot-height))]
    (dotimes [i 99] ;; Draw the 99%
      (let [bar-height (y-scale (nth pc99 i))]
        (banknotes (+ plot-x (* i bar-width) (group-offset i))
                   (+ plot-y (- plot-height bar-height) )
                   bar-width bar-height)))
    (let [n-bars 5 ;; Draw the 1%
          bar-height (y-scale (/ pc1 n-bars))]
      (banknotes (+ plot-x (* 100 bar-width) (group-offset 100))
                 (+ plot-y (- plot-height bar-height))
                 (* bar-width n-bars) bar-height))))

(defn draw-bars []
  (let [pc99    (vec (butlast wealth-distribution))
        pc1     (last wealth-distribution)
        y-max   (apply max pc99)
        y-scale (fn [x] (* (/ x y-max) plot-height))]
    (dotimes [i 99] ;; Draw the 99%
      (let [bar-height (y-scale (nth pc99 i))]
        (q/rect (+ plot-x (* i bar-width) (group-offset i))
                (+ plot-y (- plot-height bar-height) )
                bar-width bar-height)))
    (let [n-bars 5  ;; Draw the 1%
          bar-height (y-scale (/ pc1 n-bars))]
      (q/rect (+ plot-x (* 100 bar-width) (group-offset 100))
              (+ plot-y (- plot-height bar-height))
              (* bar-width n-bars) bar-height))))

(defn draw-axis-labels []
  (q/fill 0)
  (q/text-align :left)
  (q/text-size 12)
  (doseq [pc (range 0 (inc 100) 10)
          :let [offset (group-offset pc)
                x      (* pc bar-width)]]
    (q/text (str pc "%") (+ plot-x x offset) label-y))
  (q/text "\"The 1%\"" pc1-label-x  pc1-label-y))

(defn emboss-text [text x y]
  (q/fill 255)
  (q/text text x y)
  (q/fill 100)
  (q/text text x (- y 2)))

(defn draw-title []
  (q/text-size 35)
  (q/text-leading 35)
  (q/text-align :center :top)
  (emboss-text "ACTUAL DISTRIBUTION\nOF WEALTH IN THE US"
               title-x title-y))

(defn draw-shapes []
  (let [usa    (q/load-shape "resources/us-mainland.svg")
        person (q/load-shape "resources/person.svg")
        colors [(q/color 243 195 73)
                (q/color 231 119 46)
                (q/color 77  180 180)
                (q/color 231 74  69)
                (q/color 61  76  83)]]
    (.disableStyle usa)
    (.disableStyle person)
    (q/stroke 0 50)
    (q/fill 200)
    (q/shape usa 0 0)
    (dotimes [n 99]
      (let [quintile (quot n 20)
            x (-> (* n bar-width)
                  (+ plot-x)
                  (+ (group-offset n)))]
        (q/fill (nth colors quintile))
        (q/shape person x icons-y icon-width icon-height)))
    (q/shape person
             (+ plot-x (* 100 bar-width) (group-offset 100))
             icons-y icon-width icon-height)))

(defn setup []
  (q/background 255)
  (q/smooth)
  (q/no-loop))

(defn smooth-points [points]
  (let [segments (partition 4 2 points)]
    (doseq [[x1 y1 x2 y2] segments]
      (q/curve x1 y1 x1 y1 x2 y2 x2 y2))))

(defn smooth-curve [xs ys]
  (let [points (map vector xs ys)]
    (apply q/curve-vertex (first points))
    (doseq [point points]
      (apply q/curve-vertex point))
    (apply q/curve-vertex (last points))))

(defn smooth-stroke [xs ys]
  (q/begin-shape)
  (q/vertex (first xs) (first ys))
  (smooth-curve (rest xs) (rest ys))
  (q/end-shape))

(defn smooth-area [xs ys]
  (q/begin-shape)
  (q/vertex (first xs) (first ys))
  (smooth-curve (rest xs) (rest ys))
  (q/vertex (last xs) (first ys))
  (q/end-shape))

(defn plot-curve [xs ys fill-color
                  stroke-color stroke-weight]
  (let [points (map vector xs ys)]
    (q/no-stroke)
    (q/fill fill-color)
    (smooth-area xs ys)
    (q/no-fill)
    (q/stroke stroke-color)
    (q/stroke-weight stroke-weight)
    (smooth-stroke xs ys)))

(defn draw-pile [x y height]
  (q/no-stroke)
  (q/fill 80 127 64)
  (doseq [y (range (* 3 (quot (- y height) 3)) y 3)]
    (q/rect x y 6 2)))

(defn draw-map []
  (let [usa (q/load-shape "resources/us-mainland.svg")]
    (.disableStyle usa)
    (q/stroke 0 0 0 20)
    (q/fill 200 200 200)
    (q/shape usa 0 0)))

(defn emboss-text [text x y]
  (q/fill 255)
  (q/text text x y)
  (q/fill 100)
  (q/text text x (- y 2)))

(defn add-text []
  (q/text-size 20)
  (q/fill 255 255 255 200)
  (q/fill 100 100 100 200)
  (emboss-text "EXPECTED" 330 440)
  (emboss-text "IDEAL" 180 440)
  (q/text-size 35)
  (q/text-leading 35)
  (q/text-align :center :top)
  (emboss-text "ACTUAL DISTRIBUTION\nOF WEALTH IN THE US" 480 200))

(defn area-points [proportions]
  (let [f (fn [prev area]
            (-> (- area prev)
                (* 2)
                (+ prev)))
        sum (reduce + proportions)]
    (->> (reductions f (first proportions) proportions)
         (map #(/ % sum)))))

(defn point->px [offset scale]
  (fn [v]
    (-> (* v scale)
        (+ offset))))

(defn plot-areas [series px py width height]
  (let [series-ys (map area-points series)
        n-points  (count (first series-ys))
        x-scale   (point->px px (/ width (dec n-points)))
        xs        (map x-scale (range n-points))
        y-max     (apply max (apply concat series-ys))
        y-scale   (point->px py (/ height y-max -1))]
    (doseq [ys series-ys]
      (plot-curve (cons (first xs) xs)
                  (map y-scale (cons 0 ys))
                  (q/color 255 100)
                  (q/color 255 200) 3))))

(defn draw-expected-ideal []
  (let [expected [3 6.5 12 20 58.5]
        ideal    [10.5 14 21.5 22 32]]
    (plot-areas [expected ideal]
                plot-x
                (+ plot-y plot-height)
                plot-width
                (* (/ plot-height 0.075) 0.05))
    (q/text-size 20)
    (emboss-text "EXPECTED" 400 430)
    (emboss-text "IDEAL" 250 430)))


(defn plot-area [proportions px py width height]
  (let [ys      (area-points proportions)
        points  (map vector (range) ys)
        x-scale (/ width (dec (count ys)))
        y-scale (/ height (apply max ys))]
    (q/stroke 0)
    (q/fill 200)
    (q/begin-shape)
    (doseq [[x y] points]
      (q/vertex (+ px (* x x-scale))
                (- py (* y y-scale))))
    (q/end-shape)))

(defn plot-full-area [proportions px py width height]
  (let [ys      (area-points proportions)
        points  (map vector (range) ys)
        x-scale (/ width (dec (count ys)))
        y-scale (/ height (apply max ys))]
    (q/stroke 0)
    (q/fill 200)
    (q/begin-shape)
    (q/vertex 0 height)
    (doseq [[x y] points]
      (q/vertex (+ px (* x x-scale))
                (- py (* y y-scale))))
    (q/vertex width height)
    (q/end-shape)))

(defn point->px [offset scale]
  (fn [v]
    (-> (* v scale)
        (+ offset))))

(defn plot-smooth-area [proportions px py width height]
  (let [ys      (cons 0 (area-points proportions))
        points  (map vector (range) ys)
        x-scale (/ width (dec (count ys)))
        y-scale (/ height (apply max ys) -1)]
    (plot-curve (map (point->px px x-scale) (range (count ys)))
                (map (point->px py y-scale) ys)
                (q/color 200)
                (q/color 0) 2)))
