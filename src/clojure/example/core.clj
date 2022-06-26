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
                                (-> cont
                                    (.add "behold")
                                    .row)
                                (-> cont
                                    (.image (.find Core/atlas "example-clojure-mod-frog"))
                                    (.pad 20)
                                    .row)
                                (doto dialog
                                  (.button "I see" (.hide dialog)))
                                  (.show dialog))))))

(defn load-content []
  (Log/info "Loading some example content."))