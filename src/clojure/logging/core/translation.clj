(ns logging.core.translation
  "Frontend to the backends in logging.core.translatione.translation.
   Require as tl."
  (:require [clojure.string :as str]
            [logging.core.setting :refer [defsetting]]
            [logging.core.translation.deepl :as deepl]
            [logging.core.translation.libretranslate :as libretranslate]
            (logging.util [color :as color]
                          [lambdas :refer [command-runner consfn]]
                          [log :refer [debug info]]
                          [macros :refer [if-let' when-let']])
            [logging.vars :refer [is-foo]])
  (:import (arc Core Events)
           (arc.graphics Color)
           (arc.util CommandHandler Reflect)
           (java.util Locale)
           (mindustry Vars)
           (mindustry.game EventType$PlayerChatEvent)
           (mindustry.gen Call)
           (mindustry.ui.fragments ChatFragment)))

(def foo-translation
  "Whether chat translation of foo's client is enabled."
  (delay
   (and @is-foo
        (try (Reflect/get (Class/forName "mindustry.client.ClientVars") "enableTranslation")
             (catch Exception _ false)))))
(def default-backend (ref :backends/libretranslate))
(def supported-langs (ref #{"en" "fr" "de" "ru" "zh" "ja" "ko"}))
(def target-lang (ref "en"))

(defsetting enable "extra-enabletranslation" true
  #(and
    %
    (not @foo-translation)
    (not Vars/headless)))

;; Disable reflection warning, foo methods can't be statically resolved
(set! *warn-on-reflection* false)
(defn- add-message [s]
  (if @is-foo
    (.addMessage (.-chatfrag Vars/ui) s "Translation" Color/sky "" s)
    (.addMessage (.-chatfrag Vars/ui) (str "[sky]Translation:[] " s))))
(defn- err-msg-not-found []
  (if @is-foo
    (let [error "No message found."]
      (.addMessage (.-chatfrag Vars/ui) error "Error" Color/scarlet "" error))
    (.addMessage (.-chatfrag Vars/ui) "[scarlet]Error:[] no message found.")))
(set! *warn-on-reflection* true)
(defn- send-translation [s] (Call/sendChatMessage (str s " [gray](translated)")))

(defn languages [{:keys [backend callback]
                  :or   {backend @default-backend, callback identity}}]
  ((condp = backend
     :backends/libretranslate libretranslate/languages
     :backends/deepl deepl/languages)
   callback))

(defn translate [{:keys [backend s dst callback]
                  :or   {backend @default-backend, dst "en", callback identity}}]
  (debug "Translating @ to @..." s dst)
  ((condp = backend
     :backends/libretranslate libretranslate/translate
     :backends/deepl deepl/translate)
   s dst callback))

(defn set-default-backend [backend]
  (languages
   {:backend  backend
    :callback (fn [langs]
                (dosync
                 (ref-set default-backend backend)
                 (ref-set supported-langs langs)
                 (let [locale (.getLanguage (Locale/getDefault))]
                   (ref-set target-lang (if (langs locale) locale "en"))))
                (info "Switched to @ for translation" backend))}))

(defn -init []
  (when @foo-translation (info "@extra-logging.footranslation"))
  (when Vars/headless (info "@extra-logging.headlesstranslation"))
  (deepl/usage #(set-default-backend (if (< %1 %2) :backends/deepl
                                                   :backends/libretranslate)))
  (Events/on
   EventType$PlayerChatEvent
   (consfn [e]
     (when-let' [^EventType$PlayerChatEvent
                 e   e
                 _   (and @enable
                          (not= (.-player e) Vars/player))
                 msg (color/remove-colors (.-message e))]
       (translate {:s        msg
                   :dst      @target-lang
                   :callback #(when (not= % msg) (add-message %))})))))

(defn -register-client-commands [^CommandHandler handler]
  (.register
   handler "tl" "[lang] [message...]"
   (.get Core/bundle "extra-logging.command.tl.description")
   (command-runner
    (fn [[a b] _]
      ;; The command should still be usable even when translation is disabled.
      (cond
        b (if (@supported-langs a)
            (translate {:s b :dst a :callback send-translation})
            (translate {:s (str a " " b) :callback send-translation}))
        a (translate {:s a :callback send-translation})
        ;; Translate last message in chat
        :else (if-let' [msg         (first (Reflect/get
                                            ChatFragment (.-chatfrag Vars/ui)
                                            "messages"))
                        unformatted (if @is-foo (Reflect/get msg "unformatted")
                                                (second (str/split msg #":\S* " 2)))]
                (translate
                 {:s        (color/remove-colors unformatted)
                  :dst      @target-lang
                  :callback add-message})
                (err-msg-not-found)))))))
