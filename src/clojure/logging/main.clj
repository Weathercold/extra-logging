(ns logging.main
    (:import (arc.util CommandHandler Log)))

(defn main []
      (Log/info "Creating example mod."))

(defn init []
      (Log/info "Initializing mod."))

(defn load-content []
      (Log/info "Loading some example content."))

(defn register-client-commands [^CommandHandler handler]
      (Log/info "Registering client commands."))

(defn register-server-commands [^CommandHandler handler]
      (Log/info "Registering server commands."))
