(ns leiningen.copy
  (:require [clojure.java.io :as io]
            [leiningen.core.main :as main]
            [leiningen.jar :as jar]))

(def ^:private mods-folder
  (delay
   (io/file
    (condp #(.contains %1 %2) (System/getProperty "os.name")
      "Windows" (System/getenv "AppData")
      "Linux" (str (System/getenv "HOME") "/.local/share")
      "Mac OS" (str (System/getenv "HOME") "/Library/Application Support"))
    "Mindustry/mods")))

(defn copy
  "Copy the jar to the mods folder for easy testing."
  [project]
  (let [android-jar (io/file (jar/get-classified-jar-filename project :android))
        desktop-jar (io/file (jar/get-jar-filename project :standalone))
        dst-zip     (io/file @mods-folder (str (:name project) ".zip"))]
    (if-let [selected-jar (cond (.exists android-jar) android-jar
                                (.exists desktop-jar) desktop-jar)]
      (do (println "Copying" (.getAbsolutePath selected-jar)
                   "to" (.getAbsolutePath dst-zip) "...")
          (io/copy selected-jar dst-zip))
      (main/abort "No jar found."))))
