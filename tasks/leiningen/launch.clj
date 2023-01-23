(ns leiningen.launch
  (:require [clojure.java.shell :as shell]
            [clojure.string :as str]
            [leiningen.core.main :as main]))

(defn launch
  "Launch the mindustry jar specified by the environment variable `MINDUSTRY_JAR`."
  [_]
  (if-let [jar (System/getenv "MINDUSTRY_JAR")]
    (shell/sh "java" "-jar" jar)
    (main/abort
     (str/join " " ["Please set the environment variable MINDUSTRY_JAR"
                    "to the absolute path of your Mindustry jar. You can"
                    "do so in Intellij's run configuration settings."]))))
