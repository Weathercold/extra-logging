(ns logging.core.repl
  (:require [logging.core.setting :refer [defsetting]]
            [logging.util.lambdas :refer [cons1]]
            [logging.util.log :refer [err info]]
            [nrepl.core :as nrepl]
            [nrepl.server :as nrepls])
  (:import (arc Events)
           (mindustry.game EventType$DisposeEvent)))

(defsetting enable "extra-enablerepl" false)
(defsetting address "extra-repladdress" "127.0.0.1")
(defsetting port "extra-replport" "7888"
  #(try (Integer/parseInt %)
        (catch NumberFormatException e
          (err "Server port must be an integer" e) ; FIXME
          7888)))

(def server (atom nil))
(def client (delay (nrepl/client @server 1000)))

(defn eval-str [s] (nrepl/response-values (nrepl/message @client {:op "eval" :code s})))

(defn -main []
  (when @enable
    (reset! server (nrepls/start-server :bind @address :port @port))
    (info "Started REPL on @:@" @address @port)
    (Events/on EventType$DisposeEvent (cons1 (fn [_] (nrepls/stop-server @server))))))
