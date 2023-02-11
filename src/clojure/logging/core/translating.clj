(ns logging.core.translating
  "Frontend to the backends in logging.core.translating. There may be a DeepL
   wrapper in the future.
   Require as tl."
  (:require [clojure.string :as str]
            [logging.core.setting :refer [defsetting]]
            [logging.core.translating.libretranslate :as libretranslate]
            (logging.util [color :as color]
                          [lambdas :refer [command-runner cons1]]
                          [log :refer [info]]
                          [macros :refer [if-let']])
            [logging.vars :refer [is-foo]])
  (:import (arc Core Events)
           (arc.graphics Color)
           (arc.util CommandHandler Reflect)
           (java.util Locale)
           (mindustry Vars)
           (mindustry.gen Call)
           (mindustry.ui.fragments ChatFragment)))

(def foo-enable-translation
  "Whether chat translation of foo's client is enabled."
  (delay
   (and @is-foo
        (try (Reflect/get (Class/forName "mindustry.client.ClientVars") "enableTranslation")
             (catch Exception _ false)))))
(def backend :backends/libretranslate)
(def supported-langs (atom #{"en" "fr" "de" "ru" "zh" "ja" "ko"}))
(def target-lang (atom "en"))

(defsetting enable-translation "extra-enabletranslation" true
  #(and
    %
    (not @foo-enable-translation)
    (not Vars/headless)))

(defn- send-chat [s] (Call/sendChatMessage (str s " [gray](translated)")))

(defmulti languages (constantly backend))
(defmethod languages :backends/libretranslate
  ([] (libretranslate/languages))
  ([callback] (libretranslate/languages callback)))

(defmulti translate (constantly backend))
(defmethod translate :backends/libretranslate
  ([s callback] (libretranslate/translate s callback))
  ([s dst callback] (libretranslate/translate s dst callback)))

;; Disable reflection warning, foo methods can't be statically resolved
(set! *warn-on-reflection* false)
(defn -init []
  (when @foo-enable-translation (info "@extra-logging.footranslation"))
  (when Vars/headless (info "@extra-logging.headlesstranslation"))
  (languages (fn [langs]
               (reset! supported-langs langs)
               (let [locale (.getLanguage (Locale/getDefault))]
                 (when (langs locale)
                   (reset! target-lang locale)))))
  (when @is-foo
    (Events/on
     (Class/forName "mindustry.game.EventType$PlayerChatEventClient")
     (cons1 (fn [_]
              (when @enable-translation
                (when-let [msg (-> (.. Vars/ui -chatfrag -messages)
                                   (first)
                                   .-unformatted
                                   color/remove-colors)]
                  (translate msg @target-lang
                             #(when-not (= % msg)
                                (.addMessage (.-chatfrag Vars/ui)
                                             % "Translation" Color/sky "" %))))))))))

(defn -register-client-commands [^CommandHandler handler]
  (.register
   handler "tl" "[lang] [message...]"
   (.get Core/bundle "extra-logging.command.tl.description")
   (command-runner
    (fn [[a b] _]
      ;; The command should still be usable even with enable-translation set to false.
      (cond
        b (if (@supported-langs a)
            (translate b a send-chat)
            (translate (str a " " b) send-chat))
        a (translate a send-chat)
        ;; Translate last message in chat
        :else (if-let' [msg         (first (Reflect/get ChatFragment (.-chatfrag Vars/ui) "messages"))
                        unformatted (if @is-foo (.-unformatted msg)
                                                (second (str/split msg #":\S* " 2)))]
                (translate
                 (color/remove-colors unformatted)
                 @target-lang
                 #(if @is-foo
                    (.addMessage (.-chatfrag Vars/ui) % "Translation" Color/sky "" %)
                    (.addMessage (.-chatfrag Vars/ui) (str "[sky]Translation:[] " %))))
                (if @is-foo
                  (let [error "No message found."]
                    (.addMessage (.-chatfrag Vars/ui) error "Error" Color/scarlet "" error))
                  (.addMessage (.-chatfrag Vars/ui) "[scarlet]Error:[] no message found."))))))))
(set! *warn-on-reflection* true)
