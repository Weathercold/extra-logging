(ns logging.core
  (:require (logging.core [event-logging :as elog]
                          [log-handler :as log-handler]
                          [repl :as repl]
                          [settings :as settings]
                          [translation :as tl])
            [logging.util.lambdas :refer [consfn]]
            [logging.util.log :refer [debug]])
  (:import (arc Events)
           (arc.util CommandHandler)
           (mindustry.game EventType$DisposeEvent)))

(defn -main []
  (settings/refresh!)
  (log-handler/-main)
  (debug "Creating mod")
  (repl/-main)
  (elog/-main)
  (Events/on EventType$DisposeEvent (consfn [_] (shutdown-agents))))

(defn -init []
  (debug "Initializing")
  (settings/-init)
  (tl/-init))

(defn -load-content [] (debug "Loading content"))

(defn -register-client-commands [^CommandHandler handler]
  (debug "Registering commands")
  (tl/-register-client-commands handler))

(defn -register-server-commands [^CommandHandler _] (debug "Registering commands"))
