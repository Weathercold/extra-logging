(ns example.util.lambdas
  (:import (arc.func Boolc Boolf Boolf2 Boolf3 Boolp
                     Cons Cons2 Cons3 Cons4 ConsT
                     Prov)))

(defn prov [f]
  (reify Prov
    (get [_] (f))))

(defn boolp [f]
  (reify Boolp
    (get ^Boolean [_] (f))))

(defn boolf1 [f]
  (reify Boolf
    (get ^Boolean [_ a] (f a))))

(defn boolf2 [f]
  (reify Boolf2
    (get ^Boolean [_ a b] (f a b))))

(defn boolf3 [f]
  (reify Boolf3
    (get ^Boolean [_ a b c] (f a b c))))

(defn runnable [f]
  (reify Runnable
    (run [_] (f))))

(defn boolc [f]
  (reify Boolc
    (get [_ ^Boolean a] (f a))))

(defn cons1 [f]
  (reify Cons
    (get [_ a] (f a))))

(defn cons2 [f]
  (reify Cons2
    (get [_ a b] (f a b))))

(defn cons3 [f]
  (reify Cons3
    (get [_ a b c] (f a b c))))

(defn cons4 [f]
  (reify Cons4
    (get [_ a b c d] (f a b c d))))

(defn const [f]
  (reify ConsT
    (get [_ a] (f a))))