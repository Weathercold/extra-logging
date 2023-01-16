(ns logging.main
  (:require [logging.core.settings]
            [logging.core.translating :as transl]
            [logging.util.log :refer [debug]])
  (:import (arc.util CommandHandler)))

(defn main [] (debug "Creating mod"))

(defn init [] (debug "Initializing"))

(defn load-content [] (debug "Loading content"))

(defn register-client-commands [^CommandHandler handler]
  (debug "Registering commands")
  (transl/register-command handler))

(defn register-server-commands [^CommandHandler _] (debug "Registering commands"))
