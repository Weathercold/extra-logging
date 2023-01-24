(ns logging.main
  (:require (logging.core [events :as events]
                          [log-handler :as log-h]
                          [settings :as settings]
                          [translating :as transl])
            [logging.util.log :refer [debug]])
  (:import (arc.util CommandHandler)))

(defn -main []
  (settings/refresh!)
  (log-h/-main)
  (debug "Creating mod")
  (events/-main))

(defn -init []
  (debug "Initializing")
  (settings/-init)
  (transl/-init))

(defn -load-content [] (debug "Loading content"))

(defn -register-client-commands [^CommandHandler handler]
  (debug "Registering commands")
  (transl/-register-client-commands handler))

(defn -register-server-commands [^CommandHandler _] (debug "Registering commands"))
