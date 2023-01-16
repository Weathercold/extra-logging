(ns logging.core.setting
  "Always use :refer when requiring this namespace."
  (:import (arc Core)
           (clojure.lang IDeref)))

(defrecord Setting [setting default transform value]
  IDeref (deref [_] @value))

(defn set-setting [^Setting {:keys [setting default transform value]}]
  (dosync (ref-set value (transform (.get Core/settings setting default)))))

(defmacro defsetting
  ([n setting default] `(defsetting ~n ~setting ~default identity))
  ([n setting default transform]
   `(def ~n (doto (->Setting ~setting ~default ~transform (ref nil))
              set-setting))))
