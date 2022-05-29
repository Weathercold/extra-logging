(defproject example "0.1.0"
  :description "Mindustry Clojure Mod Template"
  :url "http://github.com/Weathercold/ExampleClojureModTemplate"
  :license {:name "GPL-3.0"
            :url "https://www.gnu.org/licenses/gpl-3.0.txt"}

  :repositories [["jitpack" "https://www.jitpack.io"]]
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :profiles {:dev {:dependencies [[com.github.Anuken.MindustryJitpack/core "c4b4c2e15c"]
                                  [com.github.Anuken.Arc/arc-core "e7c161da88"]]}
             :provided {:dependencies [[com.github.Anuken.MindustryJitpack/core "c4b4c2e15c"]
                                       [com.github.Anuken.Arc/arc-core "e7c161da88"]]}}

  :resource-paths ["resource"]
  :main nil
  :aot :all
  :omit-source true
  :jar-name "example-%s.zip"
  :uberjar-name "example-%s-standalone.zip")