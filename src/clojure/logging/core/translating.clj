(ns logging.core.translating
  (:require [logging.core.setting :refer [defsetting]]
            [logging.util.log :refer [info]])
  (:import (arc.util CommandHandler Reflect)
           (java.util Locale)
           (mindustry Vars)))

(def foo-has-translation
  "Whether foo client supports chat translation."
  (delay
   (try (Reflect/get (Class/forName "mindustry.client.ClientVars") "enableTranslation")
        true
        (catch Exception _ false))))
(def target-lang (delay (.getLanguage (Locale/getDefault))))
(def supported-langs (agent nil)) ; TODO: initialize

(defsetting enable-translation "extra-enabletranslation" true
  #(and
    %
    (not @foo-has-translation)
    (not Vars/headless)))

(defn -init []
  (when @foo-has-translation (info "@extra-logging.footranslation"))
  (when Vars/headless (info "@extra-logging.headlesstranslation")))

(defn -register-client-commands [^CommandHandler _])
