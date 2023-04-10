(ns example.main
  (:require [example.util.lambdas :refer [consfn]])
  (:import (arc Core Events)
           (arc.util CommandHandler Log)
           (mindustry.game EventType$ClientLoadEvent)
           (mindustry.ui.dialogs BaseDialog)))

(defn main []
  (Log/info "Creating example mod.")
  (Events/on
   EventType$ClientLoadEvent
   (consfn [_]
     (let [dialog                (BaseDialog. "frog")
           cont                  (.cont dialog)
           ^Runnable hide-dialog #(.hide dialog)]
       (doto cont
         (.. (add "behold")
             row)
         (.. (image (.find Core/atlas "example-clojure-mod-frog"))
             (pad 20.)
             row)
         (.. (button "I see" hide-dialog)
             (size 100., 50.)))
       (.show dialog)))))

(defn init []
  (Log/info "Initializing mod."))

(defn load-content []
  (Log/info "Loading some example content."))

(defn register-client-commands [^CommandHandler handler]
  (Log/info "Registering client commands."))

(defn register-server-commands [^CommandHandler handler]
  (Log/info "Registering server commands."))
