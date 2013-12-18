(ns clj-springlayout.core
  (:import [javax.swing Spring])
  (:refer-clojure :exclude [+ - * / max min]))

(defmulti spring class)

(defmethod spring Number [n]
  (Spring/constant n))

(defmethod spring Spring [s]
  s)

(defn + [& args]
  (reduce (fn
            ([a b] (Spring/sum a b))
            ([] nil))
          (map spring args)))

(defn - [& args]
  (let [springs (map spring args)]
    (if (= 1 (count springs))
      (Spring/minus (first springs))
      (reduce (fn
                ([a b] (Spring/sum a (Spring/minus b)))
                ([] nil))
              springs))))

(defn * [v1 v2]
  {:pre [(some (partial instance? Number) [v1 v2])]}
  (let [[s factor] (cond
                    (instance? Number v2) [(spring v1) v2]
                    (instance? Number v1) [(spring v2) v1])]
       (Spring/scale s factor)))

(defn / [s factor]
  {:pre [(instance? Number factor)]}
  ; TODO check if s is a numberical/constant spring and thus allow s
  ; to be the factor ( -> commutative)
  (Spring/scale (spring s) (float (clojure.core// 1 factor))))

(defn max [& args]
  (reduce (fn
           ([a b] (Spring/max a b))
           ([] nil))
          (map spring args)))

(comment
  ;TODO implement MinSpring and patch javax.swing.Spring
  (defn min [& args]
    (reduce (fn
              ([a b] (Spring/min a b))
              ([] nil))
            (map spring args))))
