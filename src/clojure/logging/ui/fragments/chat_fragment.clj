(ns logging.ui.fragments.chat-fragment
  (:require [clojure.string :as str]
            [logging.core.translating :as tl]
            [logging.util.color :as color]
            [logging.util.lambdas :refer [boolf]]
            [logging.util.macros :refer [when-let']]
            [logging.vars :refer [is-foo]])
  (:import (arc Core)
           (mindustry Vars)
           (mindustry.gen Groups Player)
           (mindustry.ui.fragments ChatFragment)))

(def instance
  (delay
   (proxy [ChatFragment] []
     (addMessage
       ([s]
        ;; Add type hint to this to avoid reflection
        (let [^ChatFragment this this]
          (proxy-super addMessage s)
          (when-let' [_           @tl/enable-translation
                      [sender message] (str/split s #":\S* " 2)
                      _           (and message
                                       (not-any? #(str/includes? sender %)
                                                 ["Translation" "Error" "Server"
                                                  (.-name Vars/player)])
                                       (some (fn [^Player player]
                                               (str/includes? sender (.-name player)))
                                             Groups/player))
                      unformatted (color/remove-colors message)]
            (tl/translate
             unformatted @tl/target-lang
             #(when-not (= % unformatted)
                (proxy-super addMessage (str "[sky]Translation:[] " %)))))))))))

(defn -init []
  (when-not (or Vars/headless @is-foo)
    (doto (.find Core/scene (boolf #(instance? ChatFragment %)))
      .clear
      .remove)
    (set! (.-chatfrag Vars/ui) @instance)
    (.build ^ChatFragment @instance (.-hudGroup Vars/ui))))
