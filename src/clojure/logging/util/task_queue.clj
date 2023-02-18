(ns logging.util.task-queue
  "Queue of Delay objects with side effects scheduled to be forced immediately
   after a transaction. (I am a genius)
   Require as tq.")

(def queue (ref []))

(defmacro soon [& body]
  `(alter queue conj (delay ~@body)))

(defn exec []
  (run! deref @queue)
  (dosync (ref-set queue [])))
