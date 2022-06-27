(ns example.util.lambdas
  (:import (arc.func Cons Cons2 Cons3 Cons4)))

(defn runnable [f]
  (reify Runnable
    (run [this] (f))))

(defn cons1 [f]
  (reify Cons
    (get [this a] (f a))))

(defn cons2 [f]
  (reify Cons2
    (get [this a b] (f a b))))

(defn cons3 [f]
  (reify Cons3
    (get [this a b c] (f a b c))))

(defn cons4 [f]
  (reify Cons4
    (get [this a b c d] (f a b c d))))