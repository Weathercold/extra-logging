(ns example.core
  (:import (arc Core Events)
           (arc.util Log Time)
           (mindustry.game EventType$ClientLoadEvent)
           (mindustry.ui.dialogs BaseDialog)))

(defn main []
  (Log/info "Loaded ExampleClojureMod constructor.")
  (Events/on EventType$ClientLoadEvent
             #(Time/runTask 10
                            (fn []
                              (let [dialog (BaseDialog. "frog")
                                    cont (.cont dialog)]
                                (doto cont
                                  (.add "behold")
                                  .row
                                  (.image (.find Core/atlas "frog")))
                                (doto dialog
                                  .addCloseButton
                                  .show))))))

(defn loadContent []
  (Log/info "Loading some example content."))