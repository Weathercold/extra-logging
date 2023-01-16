(ns logging.core.translating
  (:require [logging.core.setting :refer [defsetting]]
            [logging.util.lambdas :refer [cons1]]
            [logging.util.log :refer [info]])
  (:import (arc Events)
           (arc.util CommandHandler Reflect)
           (java.util Locale)
           (mindustry Vars)
           (mindustry.game EventType$ClientLoadEvent)))

(def foo-has-translation
  "Whether foo client supports chat translation."
  (try (Reflect/get (Class/forName "mindustry.client.ClientVars") "enableTranslation")
       true
       (catch Exception _ false)))
(def target-lang (.getLanguage (Locale/getDefault)))

(defsetting enable-translation
  "extra-enabletranslation"
  (not (or foo-has-translation Vars/headless))
  #(and
    %
    (not foo-has-translation)
    (not Vars/headless)))
(def supported-langs (agent nil)) ; TODO: initialize

(defn register-command [^CommandHandler handler])

(Events/on EventType$ClientLoadEvent
           (cons1 (fn [_]
                    (when foo-has-translation (info "@extra-logging.footranslation"))
                    (when Vars/headless (info "@extra-logging.headlesstranslation")))))
