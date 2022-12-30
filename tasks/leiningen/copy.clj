(ns leiningen.copy
  (:require [clojure.java.io :as io]
            [leiningen.core.main :as main]
            [leiningen.jar :as jar]))

(defn- find-mods []
  (let [os (System/getProperty "os.name")]
    (cond
      (.contains os "Windows") (io/file (System/getenv "AppData") "Mindustry/mods")
      (.contains os "Linux") (io/file (System/getenv "HOME") ".local/share/Mindustry/mods")
      (.contains os "Mac OS") (io/file (System/getenv "HOME") "Library/Application Support/Mindustry/mods"))))

(defn copy
  "Copy the jar to the mods folder for easy testing."
  [project]
  (let [android-jar (io/file (jar/get-classified-jar-filename project :android))
        desktop-jar (io/file (jar/get-jar-filename project :standalone))
        dst-zip (io/file (find-mods) (str (:name project) ".zip"))]
    (cond
      (.exists android-jar) (do (io/copy android-jar dst-zip)
                                (print "Copied" (.getAbsolutePath android-jar)))
      (.exists desktop-jar) (do (io/copy desktop-jar dst-zip)
                                (print "Copied" (.getAbsolutePath desktop-jar)))
      :else (main/abort "No jar found."))
    (println " to" (.getAbsolutePath dst-zip))))
