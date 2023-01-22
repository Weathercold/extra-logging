(ns logging.core.settings
  "Settings menu.
   Note that although all the vars are thread-safe, this mod is largely
   single-threaded like the game itself, so thread-safety is not really a
   concern. (why did I use refs then)"
  (:require (logging.core [events :as events]
                          [log-handler :as log-h]
                          [setting :refer [set-setting]]
                          [translating :as transl])
            (logging.util [lambdas :refer [cons1 s-proc]]
                          [log :as log]))
  (:import (arc Core Events)
           (arc.util Log Log$LogLevel)
           (logging.core.setting Setting)
           (mindustry Vars)
           (mindustry.game EventType$ClientLoadEvent)
           (mindustry.gen Icon)
           (mindustry.ui.dialogs SettingsMenuDialog$SettingsTable
                                 SettingsMenuDialog$SettingsTable$Setting)))

(defn- register [^SettingsMenuDialog$SettingsTable st]
  (letfn [(add-category [s]
            (.pref st (proxy [SettingsMenuDialog$SettingsTable$Setting] [s]
                        (add [^SettingsMenuDialog$SettingsTable st]
                          (doto st
                            (.. (add "") row)
                            (.add (str "[accent] " (.get Core/bundle (str "category." s))))
                            .row)))))
          (check-pref [^Setting {:keys [setting default]}]
            (.checkPref st setting default))
          (slider-pref [^Setting {:keys [setting default]} mn mx sproc]
            (.sliderPref st setting default mn mx sproc))
          (text-pref [^Setting {:keys [setting default]}]
            (.textPref st setting default))
          (area-pref [^Setting {:keys [setting default]}]
            (.areaTextPref st setting default))]
    (add-category "extra-meta")
    (check-pref log/enable-meta-debugging)
    (text-pref log/meta-color)

    (add-category "extra-logging")
    (slider-pref log-h/log-level 0 4
                 (s-proc #(.name ^Enum (nth (Log$LogLevel/values) %))))
    (check-pref log-h/colored-terminal)
    (area-pref log-h/terminal-format)
    (area-pref log-h/console-format)
    (text-pref log-h/time-formatter)

    (add-category "extra-eventlogging")
    (check-pref events/enable)
    (slider-pref events/log-level 0 4
                 (s-proc #(.name ^Enum (nth (Log$LogLevel/values) %))))
    (area-pref events/listening-events)

    (add-category "extra-translation")
    (check-pref transl/enable-translation)))

(defn refresh! []
  (dosync
   (set-setting log/enable-meta-debugging)
   (set-setting log/meta-color)

   (set-setting log-h/log-level)
   (set-setting log-h/colored-terminal)
   (set-setting log-h/terminal-format)
   (set-setting log-h/console-format)
   (set-setting log-h/time-formatter)

   (set-setting events/enable)
   (set-setting events/log-level)

   (set-setting transl/enable-translation))
  (set! Log/level @log-h/log-level))

(Events/on EventType$ClientLoadEvent
           (cons1 (fn [_]
                    (doto (.-settings Vars/ui)
                      (.addCategory "@extra-logging.displayname" Icon/wrench (cons1 register))
                      (.hidden refresh!)))))
