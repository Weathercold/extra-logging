(defproject extra-logging "2.0.1"
  :description "Extra Logging"
  :url "http://github.com/Weathercold/extra-logging"
  :license {:name "GPL-3.0-or-later"
            :url  "https://www.gnu.org/licenses/gpl-3.0.txt"}

  :repositories {"jitpack" "https://www.jitpack.io"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "2.4.0"]
                 [org.flatland/ordered "1.15.10"]
                 [http-kit "2.7.0-alpha1"]
                 [nrepl "1.1.0-alpha1"]]
  :profiles {:user     {:plugins [[lein-kibit "0.1.8"]]}
             :provided {:dependencies [[com.github.Anuken.Mindustry/core "v143.1"]
                                       [com.github.Anuken.Arc/arc-core "v143.1"]
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
