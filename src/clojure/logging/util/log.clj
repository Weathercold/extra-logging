(ns logging.util.log
  "Wrapper functions around arc.util.Log.
   Always use :refer for log functions."
  (:require [clojure.stacktrace :as st]
            [clojure.string :as str]
            [logging.core.setting :refer [defsetting]])
  (:import (arc Core)
           (arc.util Log Log$LogLevel)))

;; https://clojure.atlassian.net/browse/CLJ-440
(def ^:private -- (object-array 0))

(defsetting enable-meta-debugging "extra-enablemetadebugging" false)
(defsetting meta-color "extra-metacolor" "[accent]")

(defmulti log
  (fn [lvl & [a b]]
    (if (and (= lvl (Log$LogLevel/debug)) (not @enable-meta-debugging))
      :noop
      (cond
        (instance? String a) (if (instance? Throwable b) :strth :format)
        (instance? Throwable a) :th
        :else :trace))))

(defmethod log :format [^Log$LogLevel lvl ^String text & args]
  (if (str/starts-with? text "@")
    (Log/log lvl (str @meta-color "[EL][] " (.format Core/bundle (subs text 1) args)) --)
    (Log/log lvl (str @meta-color "[EL][] " text) (to-array args))))

(defmethod log :th [^Log$LogLevel lvl ^Throwable th]
  (log lvl "" th))

(defmethod log :strth [^Log$LogLevel lvl ^String text ^Throwable th]
  (Log/log lvl
           (str @meta-color "[EL][] "
                (when (seq text) (str text "\n"))
                (with-out-str (st/print-cause-trace th 7)))
           --))

(defmethod log :trace [^Log$LogLevel lvl & args]
  (Log/log lvl (str @meta-color "[EL][] " (str/join "\n" args)) --))

(defmethod log :noop [& _])

(def debug (partial log Log$LogLevel/debug))
(def info (partial log Log$LogLevel/info))
(def warn (partial log Log$LogLevel/warn))
(def err (partial log Log$LogLevel/err))
