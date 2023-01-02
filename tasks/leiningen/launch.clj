(ns leiningen.launch
  (:require [clojure.java.shell :as shell]))

(defn launch
  "Launch the mindustry jar specified by the environment variable `MINDUSTRY_JAR`."
  [_]
  (if-let [jar (System/getenv "MINDUSTRY_JAR")]
    (shell/sh "java" "-jar" jar)
    (do (println "Please set the environment variable MINDUSTRY_JAR"
                 "to the absolute path of your Mindustry jar. You can"
                 "do so in Intellij's run configuration settings.")
        (System/exit 1))))
