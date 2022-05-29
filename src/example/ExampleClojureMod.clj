(ns example.ExampleClojureMod
  (:gen-class
    :extends mindustry.mod.Mod
    :main false
    :load-impl-ns false
    :prefix "mod-"
    :init cst))

(defn mod-cst [context]
  (println "Hello World!")
  [context])

(defn mod-init []
  (println clojure-version)
  (println (* 2 3)))