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
  (:import (arc Core)
           (arc.util Log Log$LogLevel)
           (logging.core.setting Setting)
           (mindustry Vars)
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
   (run! set-setting [log/enable-meta-debugging
                      log/meta-color

                      log-h/log-level
                      log-h/colored-terminal
                      log-h/terminal-format
                      log-h/console-format
                      log-h/time-formatter

                      events/enable
                      events/log-level

                      transl/enable-translation]))
  (set! Log/level @log-h/log-level))

(defn -init []
  (when-not Vars/headless
    (doto (.-settings Vars/ui)
      (.addCategory "@extra-logging.displayname" Icon/wrench (cons1 register))
      (.hidden refresh!))))
