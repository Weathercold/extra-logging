(ns logging.core
  (:require (logging.core [events-log :as events-log]
                          [log-handler :as log-handler]
                          [repl :as repl]
                          [settings :as settings]
                          [translating :as tl])
            [logging.ui.fragments.chat-fragment :as chat-fragment]
            [logging.util.lambdas :refer [cons1]]
            [logging.util.log :refer [debug]])
  (:import (arc Events)
           (arc.util CommandHandler)
           (mindustry.game EventType$DisposeEvent)))

(defn -main []
  (settings/refresh!)
  (log-handler/-main)
  (debug "Creating mod")
  (repl/-main)
  (events-log/-main)
  (Events/on EventType$DisposeEvent (cons1 (fn [_] (shutdown-agents)))))

(defn -init []
  (debug "Initializing")
  (settings/-init)
  (tl/-init)
  (chat-fragment/-init))

(defn -load-content [] (debug "Loading content"))

(defn -register-client-commands [^CommandHandler handler]
  (debug "Registering commands")
  (tl/-register-client-commands handler))

(defn -register-server-commands [^CommandHandler _] (debug "Registering commands"))
