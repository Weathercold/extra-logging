(ns logging.core.log-handler
  "The log handler that replaces arc.util.Log/logger."
  (:require [clojure.string :as str]
            [logging.core.setting :refer [defsetting]]
            (logging.util [color :as color]
                          [lambdas :refer [consfn]]
                          [log :refer [err]]
                          [task-queue :as tq]))
  (:import (arc Events)
           (arc.util Log Log$LogHandler Log$LogLevel Reflect)
           (java.time LocalTime)
           (java.time.format DateTimeFormatter)
           (mindustry Vars)
           (mindustry.game EventType$ClientLoadEvent)))

(def level->code
  (zipmap (Log$LogLevel/values)
          ["&lc" "&lb" "&ly" "&lr" "&lw"]))
(def level->tag
  (zipmap (Log$LogLevel/values)
          ["[green]" "[royal]" "[yellow]" "[scarlet]" "[lightgray]"]))
(def level->icon
  (zipmap (Log$LogLevel/values)
          ["D", "I", "W", "E", "/"]))
(def client-loaded (atom false))
(def log-buffer (atom [] :validator #(or (not @client-loaded) (empty? %))))
(def has-new-console (ref false))
(def nc-log-buffer (ref nil))

(defsetting log-level "extra-loglevel" 0
  (fn [ord]
    (doto (nth (Log$LogLevel/values) ord)
      (#(tq/soon (set! Log/level %))))))
(defsetting colored-terminal "extra-coloredterminal" true)
(defsetting terminal-format "extra-terminalformat" "&lw[$t]&fr &fb$L[$l]&fr $m&fr")
(defsetting console-format "extra-consoleformat" "[gray][$t][] $L[$l][] $m")
(defsetting time-formatter "extra-timeformat" "HH:mm:ss.SSS"
  #(try (DateTimeFormatter/ofPattern %)
        (catch Exception e
          (tq/soon (err "Time format invalid" e))
          DateTimeFormatter/ISO_LOCAL_TIME)))

(def instance
  (reify Log$LogHandler
    (log [_ lvl s]
     ;; New Console compatibility
      (when-not (str/starts-with? s "\u0019")
        (let [timestamp (.format ^DateTimeFormatter @time-formatter (LocalTime/now))
              term-msg  (-> @terminal-format
                            (str/replace "$t" timestamp)
                            (str/replace "$L" (level->code lvl))
                            (str/replace "$l" (level->icon lvl))
                            (str/replace "$m" s)
                            (#(if @colored-terminal
                                (-> % color/str-code->escseq color/str-tag->escseq)
                                (color/remove-colors %))))
              cons-msg  (-> @console-format
                            (str/replace "$t" timestamp)
                            (str/replace "$L" (level->tag lvl))
                            (str/replace "$l" (level->icon lvl))
                            (str/replace "$m" s)
                            color/str-escseq->tag
                            color/str-code->tag)]
          (println term-msg)
          (when-not Vars/headless
            (if @client-loaded
              (.. Vars/ui -consolefrag (addMessage cons-msg))
              (swap! log-buffer conj cons-msg))
            (when @has-new-console
              (.. ^StringBuilder @nc-log-buffer (append cons-msg) (append "\n")))))))))

(defn -main []
  (set! Log/logger instance)
  (Events/on
   EventType$ClientLoadEvent
   (consfn [_]
     (reset! client-loaded true)
     (run! #(.. Vars/ui -consolefrag (addMessage %)) @log-buffer)
     (reset! log-buffer [])
     (set! Log/logger instance)
     (when-some [new-console (.getMod Vars/mods "newconsole")]
       (dosync
        (ref-set has-new-console true)
        (as-> (.-loader new-console) %
              (Class/forName "newconsole.ui.dialogs.Console" true %)
              (Reflect/get % "logBuffer")
              (ref-set nc-log-buffer %)))))))
