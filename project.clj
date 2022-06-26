(defproject example "0.1.0"
  :description "Mindustry Clojure Mod Template"
  :url "http://github.com/Weathercold/ExampleClojureModTemplate"
  :license {:name "GPL-3.0-or-later"
            :url "https://www.gnu.org/licenses/gpl-3.0.txt"}

  :repositories [["jitpack" "https://www.jitpack.io"]]
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :profiles {:provided {:dependencies [[com.github.Anuken.MindustryJitpack/core "b621383906"]
                                       [com.github.Anuken.Arc/arc-core "b4b4709321"]
                                       ; For task linting, you can remove this
                                       [leiningen "2.9.8"]]}}

  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :resource-paths ["assets"]
  :jar-name "example-%s-nodeps.jar"
  :uberjar-name "example-%s-desktop.jar"

  :main nil
  :javac-options ["-source" "8" "-target" "8" "-Xlint:-options"]
  :min-lein-version "2.0.0"

  ;; Uncomment for AOT (default)
  :aot :all
  :omit-source true

  ;; Uncomment for JIT
  ;; :jar-exclusions [#"\.java"]
  )