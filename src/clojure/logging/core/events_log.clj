(ns logging.core.events-log
  (:require [clojure.string :as str]
            [logging.core.setting :refer [defsetting]]
            [logging.util.lambdas :refer [consfn]]
            [logging.util.log :refer [err log]]
            [logging.util.task-queue :as tq])
  (:import (arc Events)
           (arc.util Log$LogLevel)
           (java.lang.reflect Field)))

(defn- str->event [s]
  (try (Class/forName
        (if (str/includes? s ".") s (str "mindustry.game.EventType$" s)))
       (catch Exception e
         (tq/soon (err (str "Cannot get event class with name " s) e)))))

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
    (run! #(Events/on % (consfn [e] (log @log-level (trace e))))
          @listening-events)))
