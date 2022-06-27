(ns leiningen.launch
  (:require [clojure.java.shell :as shell]))

(defn launch
  "Launch the mindustry jar specified by the environment variable `MINDUSTRY_JAR`."
  [_]
  (shell/sh "java" "-jar" (System/getenv "MINDUSTRY_JAR")))