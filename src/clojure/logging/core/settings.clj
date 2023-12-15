(ns logging.core.settings
  "Settings menu.
   Note that although all the vars are thread-safe, this mod is largely
   single-threaded like the game itself, so thread-safety is not really a
   concern. (why did I use refs then)"
  (:require (logging.core [event-logging :as elog]
                          [log-handler :as log-handler]
                          [repl :as repl]
                          [setting :refer [set-setting]]
                          [translation :as tl])
            [logging.core.translation.deepl :as deepl]
            (logging.util [lambdas :refer [cons1 s-proc]]
                          [log :as log]
                          [task-queue :as tq]))
  (:import (arc Core)
           (arc.util Log$LogLevel)
           (logging.core.setting Setting)
           (mindustry Vars)
           (mindustry.gen Icon)
           (mindustry.ui.dialogs SettingsMenuDialog$SettingsTable
                                 SettingsMenuDialog$SettingsTable$Setting)))

(defn- category ^SettingsMenuDialog$SettingsTable$Setting [s]
  (proxy [SettingsMenuDialog$SettingsTable$Setting] [s]
    (add [^SettingsMenuDialog$SettingsTable st]
      (doto st
        (.. (add "") row)
        (.add (str "[accent] " (.get Core/bundle (str "category." s))))
        .row))))

(defn- register [^SettingsMenuDialog$SettingsTable st]
  (letfn [(add-category [s]
            (.pref st (category s)))
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
    (slider-pref log-handler/log-level 0 4
                 (s-proc #(.name ^Enum (nth (Log$LogLevel/values) %))))
    (check-pref log-handler/colored-terminal)
    (area-pref log-handler/terminal-format)
    (area-pref log-handler/console-format)
    (text-pref log-handler/time-formatter)

    (add-category "extra-repl")
    (check-pref repl/enable)
    (text-pref repl/address)
    (text-pref repl/port)

    (add-category "extra-eventlogging")
    (check-pref elog/enable)
    (slider-pref elog/log-level 0 4
                 (s-proc #(.name ^Enum (nth (Log$LogLevel/values) %))))
    (area-pref elog/listening-events)

    (add-category "extra-translation")
    (check-pref tl/enable)
    (text-pref deepl/api-key)))

(defn refresh! []
  (dosync
   (run! set-setting
         [log/enable-meta-debugging
          log/meta-color

          log-handler/log-level
          log-handler/colored-terminal
          log-handler/terminal-format
          log-handler/console-format
          log-handler/time-formatter

          repl/enable
          repl/address
          repl/port

          elog/enable
          elog/log-level
          elog/listening-events

          tl/enable
          deepl/api-key]))
  (tq/exec))

(defn -init []
  (when-not Vars/headless
    (doto (.-settings Vars/ui)
      (.addCategory "@extra-logging.displayname" Icon/wrench (cons1 register))
      (.hidden refresh!))))
