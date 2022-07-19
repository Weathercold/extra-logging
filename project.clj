(defproject example "0.1.0"
  :description "Mindustry Clojure Mod Template"
  :url "http://github.com/Weathercold/ExampleClojureModTemplate"
  :license {:name "GPL-3.0-or-later"
            :url  "https://www.gnu.org/licenses/gpl-3.0.txt"}

  :repositories [["jitpack" "https://www.jitpack.io"]]
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :profiles {:provided {:dependencies [[com.github.Anuken.MindustryJitpack/core "0243dc7f12"]
                                       [com.github.Anuken.Arc/arc-core "47925a7b91"]
                                       ;; For tasks, you can remove this
                                       [leiningen "2.9.8"]]}}

  :main nil
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :resource-paths ["assets"]
  ;; Do not set :jar-name as it will break jar resolution in tasks
  :uberjar-name ~(-> #"defproject (\w*)"
                   (clojure.core/re-find (slurp "project.clj"))
                   second
                   (str "-%s-desktop.jar"))

  :min-lein-version "2.0.0"
  :javac-options ["-source" "8" "-target" "8" "-Xlint:-options"]
  :aliases {"uberdex" ["do" "uberjar," "dex"]
            "run-jar" ["do" "uberjar," "copy," "launch"]
            "run-dex" ["do" "uberjar," "dex," "copy," "launch"]}

  ;; Uncomment for AOT (default)
  :aot :all
  :omit-source true

  ;; Uncomment for JIT
  ;; :jar-exclusions [#"\.java"]
  )