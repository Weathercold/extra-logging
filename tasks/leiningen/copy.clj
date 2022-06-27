(ns leiningen.copy
  (:require [clojure.java.io :as io]
            [leiningen.jar :as jar]))

(defn- find-mods []
  (let [os (System/getProperty "os.name")]
    (cond
      (.contains os "Windows") (io/file (System/getenv "AppData") "Mindustry/mods")
      (.contains os "Linux") (io/file (System/getenv "HOME") ".local/share/Mindustry/mods")
      (.contains os "MacOS") (io/file (System/getenv "HOME") "Library/Application Support/Mindustry/mods"))))

(defn copy
  "Copy the jar to the mods folder for easy testing."
  [project]
  (let [android-jar (io/file (jar/get-classified-jar-filename project :android))
        desktop-jar (io/file (jar/get-jar-filename project :standalone))
        dst-jar (io/file (find-mods) (str (:name project) ".zip"))]
    (if (and (.exists android-jar)
             (or (not (.exists desktop-jar))
                 (> (.lastModified android-jar)
                    (.lastModified desktop-jar))))
      (do (io/copy android-jar dst-jar)
          (println "Copied" (.getAbsolutePath android-jar)
                   "to" (.getAbsolutePath dst-jar)))
      (do (io/copy desktop-jar dst-jar)
          (println "Copied" (.getAbsolutePath desktop-jar)
                   "to" (.getAbsolutePath dst-jar))))))