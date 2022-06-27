(ns example.core
  (:require [example.util.lambdas :refer :all])
  (:import (arc Core Events)
           (arc.util Log)
           (mindustry.game EventType$ClientLoadEvent)
           (mindustry.ui.dialogs BaseDialog)))

(defn main []
  (Log/info "Loaded ExampleClojureMod constructor.")
  (Events/on
    EventType$ClientLoadEvent
    (cons1
      (fn [_]
        (let [dialog (BaseDialog. "frog")
              cont (.cont dialog)]
          (doto cont
            (-> (.add "behold")
                .row)
            (-> (.image (.find Core/atlas "example-clojure-mod-frog"))
                (.pad 20.)
                .row)
            (-> (.button "I see" (runnable #(.hide dialog)))
                (.size 100., 50.)))
          (.show dialog))))))

(defn load-content []
  (Log/info "Loading some example content."))