(ns logging.core.translating.libretranslate
  "Libretranslate wrapper."
  (:require [clojure.data.json :as json]
            [flatland.ordered.map :refer [ordered-map]]
            [logging.util.lambdas :refer [task]]
            [logging.util.log :refer [debug err log warn]]
            [org.httpkit.client :as http])
  (:import (arc.util Log$LogLevel Timer)))

(def servers
  "List of mirrors can be found at https://github.com/LibreTranslate/LibreTranslate#mirrors.
   If you see a mirror not working, please make a pr."
  (apply ordered-map
         (interleave [;; "https://libretranslate.com" requires API key :(
                      ;; Thanks to Allen for hosting this instance and Nautilus
                      ;; for giving permission to use it
                      ;; https://github.com/TomtheCoder2/mainPlugin/blob/master/src/main/java/mindustry/plugin/minimods/Translate.java#L219
                      "http://168.119.234.142:5000"
                      "https://translate.argosopentech.com"
                      "https://translate.terraprint.co"
                      "https://lt.vern.cc"
                      "https://libretranslate.de"]
                     (repeatedly #(atom true)))))

(defn- fetch [api & [opts callback]]
  (if-let [server (some (fn [[k v]] (when @v k)) servers)]
    (http/request
     (merge {:url     (str server api)
             :headers {"Content-Type" "application/json"}
             :timeout 3000}
            opts)
     (fn [{:keys [status body error] {:keys [url]} :opts}]
       (if error
         (err "Unknown client-side error." error)
         (let [[lvl ret]
               (condp = status
                 200 [Log$LogLevel/debug
                      ((or callback identity) (json/read-str body :key-fn keyword))]
                 400 (do (err "Bad request. Aborting translation.")
                         [Log$LogLevel/err])
                 429 (do
                       (warn "Rate limit reached with server @. Retrying..." url)
                       (reset! (servers server) false)
                       (.scheduleTask (Timer/instance)
                                      (task #(reset! (servers server) true))
                                      60.)
                       [Log$LogLevel/debug @(fetch api opts callback)])
                 500 (do (err "Detection error. Aborting translation.")
                         [Log$LogLevel/err])
                 :else (do
                         (err "Unknown error. Disabling translation for this session.")
                         ;; FIXME: ugly hack to workaround reverse dependency
                         (dosync (ref-set (:value (resolve 'logging.core.translating/enable-translation)) false))
                         [Log$LogLevel/err]))]
           (log lvl "Response from @:\n@" url body)
           ret))))
    (warn "No server available. Aborting translation.")))

(defn languages [callback]
  (fetch "/languages" {} #(callback (set (map :code %)))))

(defn translate [s dst callback]
  (debug "Translating @ to @..." s dst)
  (fetch "/translate"
         {:method :post
          :body   (json/write-str {:q      s
                                   :source "auto"
                                   :target dst}
                                  :escape-unicode false)
          :filter (http/max-body-filter 1024)}
         #(callback (:translatedText %))))
