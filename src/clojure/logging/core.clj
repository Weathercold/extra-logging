(ns logging.core
  (:require [logging.util.lambdas :refer :all])
  (:import (arc.util Log)))

(defn main []
  (Log/info "Loaded ExampleClojureMod constructor."))

(defn load-content []
  (Log/info "Loading some example content."))