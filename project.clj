(defproject extra-logging "2.0.0-alpha"
  :description "Extra Logging"
  :url "http://github.com/Weathercold/extra-logging"
  :license {:name "GPL-3.0-or-later"
            :url  "https://www.gnu.org/licenses/gpl-3.0.txt"}

  :repositories [["jitpack" "https://www.jitpack.io"]]
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :profiles {:provided {:dependencies [[com.github.Anuken.MindustryJitpack/core "v141.2"]
                                       [com.github.Anuken.Arc/arc-core "v141.2"]
                                       ;; This is for task linting, you can remove this
                                       [leiningen "2.10.0"]]}}

  :main nil
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :resource-paths ["assets"]
  ;; Do not set :jar-name as it will break jar resolution in tasks
  :uberjar-name "extra-logging-%s-desktop.jar"

  :min-lein-version "2.0.0"
  :global-vars {*warn-on-reflection* true}
  ;; https://clojure.org/reference/compilation#directlinking
  :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
  :javac-options ["-source" "8" "-target" "8" "-Xlint:-options"]
  :aliases {"uberdex" ["do" "uberjar," "dex"]
            "run-jar" ["do" "uberjar," "copy," "launch"]
            "run-dex" ["do" "uberjar," "dex," "copy," "launch"]}

  ;; Uncomment for AOT (default)
  :aot :all
  :omit-source true)

  ;; Uncomment for JIT
  ;;:jar-exclusions [#"\.java"])
