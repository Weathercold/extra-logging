(defproject example "0.1.0-SNAPSHOT"
  :description "Mindustry Clojure Mod Template"
  :url "http://github.com/Weathercold/mindustry-clj-mod-template"
  :license {:name "GPL-3.0-or-later"
            :url  "https://www.gnu.org/licenses/gpl-3.0.txt"}

  :repositories {"zelaux" "https://raw.githubusercontent.com/Zelaux/MindustryRepo/master/repository"
                 "jitpack" "https://www.jitpack.io"}
  :dependencies [;; This is the last clojure version that works on Android...
                 [org.clojure/clojure "1.8.0"]]
  :profiles {:provided {:dependencies [[com.github.Anuken.Mindustry/core "v145.1"]
                                       [com.github.Anuken.Arc/arc-core "v145.1"]
                                       ;; This is for task linting, you can remove this
                                       [leiningen "2.10.0"]]}}

  :main nil
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :resource-paths ["assets"]
  ;; Do not set :jar-name as it will break jar resolution in tasks
  :uberjar-name "example-%s-desktop.jar"

  :min-lein-version "2.10.0"
  :global-vars {*warn-on-reflection* true}
  :javac-options ["-source" "8" "-target" "8" "-Xlint:-options"]
  :aliases {"uberdex" ["do" "uberjar," "dex"]
            "run-jar" ["do" "uberjar," "copy," "launch"]
            "run-dex" ["do" "uberjar," "dex," "copy," "launch"]}

  :aot :all
  :omit-source true)
