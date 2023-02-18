(ns logging.util.lambdas
  "Wrapper functions that construct objects that implement arc.func.* .
   Always use :refer when requiring this namespace.
   Sorted by type, then arity."
  (:import (arc.func Boolc Boolf Boolf2 Boolf3 Boolp
                     Cons Cons2 Cons3 Cons4 ConsT
                     FloatFloatf Floatc Floatc2 Floatc4 Floatf Floatp
                     Prov)
           (arc.util CommandHandler$CommandRunner Timer$Task)
           (mindustry.ui.dialogs SettingsMenuDialog$StringProcessor)))

;; region Providers

(defn prov ^Prov [f]
  (reify Prov
    (get [_] (f))))

(defn boolp ^Boolp [f]
  (reify Boolp
    (get [_] (f))))

(defn boolf ^Boolf [f]
  (reify Boolf
    (get [_ a] (f a))))

(defn boolf2 ^Boolf2 [f]
  (reify Boolf2
    (get [_ a b] (f a b))))

(defn boolf3 ^Boolf3 [f]
  (reify Boolf3
    (get [_ a b c] (f a b c))))

(defn floatp ^Floatp [f]
  (reify Floatp
    (get [_] (f))))

(defn floatf ^Floatf [f]
  (reify Floatf
    (get [_ a] (f a))))

(defn floatffloatf ^FloatFloatf [f]
  (reify FloatFloatf
    (get [_ a] (f a))))

(defn s-proc ^SettingsMenuDialog$StringProcessor [f]
  (reify SettingsMenuDialog$StringProcessor
    (get [_ a] (f a))))

;; endregion

;; Not exactly a lambda but ok
(defn task ^Timer$Task [f]
  (proxy [Timer$Task] []
    (run [] (f))))

;; region Consumers

(defn cons1 ^Cons [f]
  (reify Cons
    (get [_ a] (f a))))

(defn cons2 ^Cons2 [f]
  (reify Cons2
    (get [_ a b] (f a b))))

(defn cons3 ^Cons3 [f]
  (reify Cons3
    (get [_ a b c] (f a b c))))

(defn cons4 ^Cons4 [f]
  (reify Cons4
    (get [_ a b c d] (f a b c d))))

(defn cons-th ^ConsT [f]
  (reify ConsT
    (get [_ a] (f a))))

(defn command-runner ^CommandHandler$CommandRunner [f]
  (reify CommandHandler$CommandRunner
    (accept [_ args player] (f args player))))

(defn boolc ^Boolc [f]
  (reify Boolc
    (get [_ a] (f a))))

(defn floatc ^Floatc [f]
  (reify Floatc
    (get [_ a] (f a))))

(defn floatc2 ^Floatc2 [f]
  (reify Floatc2
    (get [_ a b] (f a b))))

(defn floatc4 ^Floatc4 [f]
  (reify Floatc4
    (get [_ a b c d] (f a b c d))))

;; endregion

;; Experimental
(defmacro consfn [bindings & body]
  `(reify Cons
     (get ~(into ['this] bindings) ~@body)))
