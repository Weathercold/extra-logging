(ns logging.core.log-handler
  "The log handler that replaces arc.util.Log/logger."
  (:require [clojure.string :as str]
            [logging.core.setting :refer [defsetting]]
            [logging.util.color :as color]
            [logging.util.lambdas :refer [cons1]]
            [logging.util.log :refer [err]])
  (:import (arc Events)
           (arc.util Log Log$LogHandler Log$LogLevel)
           (java.time LocalTime)
           (java.time.format DateTimeFormatter)
           (mindustry Vars)
           (mindustry.game EventType$ClientLoadEvent)))

(def level->code (zipmap (Log$LogLevel/values)
                         ["&lc" "&lb" "&ly" "&lr" "&lw"]))
(def level->tag (zipmap (Log$LogLevel/values)
                        ["[green]" "[royal]" "[yellow]" "[scarlet]" "[lightgray]"]))
(def level->icon (zipmap (Log$LogLevel/values)
                         ["D", "I", "W", "E", "/"]))

(def client-loaded (ref false))
(def log-buffer (ref [] :validator #(or (= @client-loaded false) (empty? %))))
(defsetting log-level "extra-loglevel" 0 #(nth (Log$LogLevel/values) %))
(defsetting colored-terminal "extra-coloredterminal" true)
(defsetting terminal-format "extra-terminalformat" "&lw[$t]&fr &fb$L[$l]&fr $m&fr")
(defsetting console-format "extra-consoleformat" "[gray][$t][] $L[$l][] $m")
(defsetting time-formatter "extra-timeformat" "HH:mm:ss:SSS"
  #(try (DateTimeFormatter/ofPattern %)
        (catch Exception e
          (if @client-loaded
            ;; FIXME: ugh side-effect inside transaction
            (err "Time format invalid" e)
            (Events/on EventType$ClientLoadEvent
                       (cons1 (fn [_] (err "Time format invalid" e)))))
          DateTimeFormatter/ISO_LOCAL_TIME)))

(set! Log/logger
      (reify Log$LogHandler
        (log [_ lvl s]
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
                (dosync (alter log-buffer conj cons-msg))))))))

(Events/on EventType$ClientLoadEvent
           (cons1 (fn [_] (run! #(.. Vars/ui -consolefrag (addMessage %))
                                (dosync
                                 (let [logs @log-buffer]
                                   (ref-set client-loaded true)
                                   (ref-set log-buffer [])
                                   logs))))))
