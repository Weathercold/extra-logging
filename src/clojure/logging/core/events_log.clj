(ns logging.core.events-log
  (:require [clojure.string :as str]
            [logging.core.setting :refer [defsetting]]
            [logging.util.lambdas :refer [cons1]]
            [logging.util.log :refer [err log]])
  (:import (arc Events)
           (arc.util Log$LogLevel)
           (java.lang.reflect Field)))

(defn- str->event [s]
  (try (Class/forName
        (if (str/includes? s ".") s (str "mindustry.game.EventType$" s)))
       (catch Exception e
         ;; FIXME: ugh side effect inside transaction
         (err (str "Cannot get event class with name " s) e))))

(defn- trace [o]
  (apply str
         (.getSimpleName (class o))
         (apply concat
                (for [^Field field (.getDeclaredFields (class o))]
                  (try [" " (.getName field) "=" (.get field o)]
                       (catch Exception _))))))

(defsetting enable "extra-enableeventlogging" false)
(defsetting log-level "extra-eventloglevel" 0 #(nth (Log$LogLevel/values) %))
(defsetting listening-events
  "extra-listeningevents"
  (str/replace "FileTreeInitEvent
                ContentInitEvent
                WorldLoadEvent
                ClientLoadEvent
                ClientPreConnectEvent
                StateChangeEvent
                DisposeEvent"
               #" +" "")
  #(->> (str/split % #"\s+")
        (map str->event)
        (remove nil?)))

(defn -main []
  (when @enable
    (run! (fn [c] (Events/on c (cons1 #(log @log-level (trace %)))))
          @listening-events)))
