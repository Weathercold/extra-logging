(ns logging.core.translation.deepl
  (:require [clojure.string :as str]
            [jsonista.core :as j]
            [logging.core.setting :refer [defsetting]]
            [logging.util.log :refer [err log warn]]
            [org.httpkit.client :as http])
  (:import (arc.util Log$LogLevel)))

(defsetting api-key "extra-deeplapikey" "")

(defn- fetch [api opts callback]
  (http/request
   (merge {:url     (str (if (str/ends-with? @api-key ":fx")
                           "https://api-free.deepl.com/v2"
                           "https://api.deepl.com/v2")
                         api)
           :headers {"Content-Type"  "application/json"
                     "Authorization" (str "DeepL-Auth-Key " @api-key)}
           :timeout 3000}
          opts)
   (fn [{:keys [status body error] {:keys [url]} :opts}]
     (if error
       (err "Unknown client-side error." error)
       (let [[lvl ret]
             (condp = status
               200 [Log$LogLevel/debug
                    (callback (j/read-value body j/keyword-keys-object-mapper))]
               429 (do
                     (warn "Rate limit reached. Retrying...")
                     [Log$LogLevel/debug @(fetch api opts callback)])
               (do
                 (err "Unknown error. Falling back on libretranslate for this session.")
                 ;; FIXME
                 ((resolve 'logging.core.translation/set-default-backend) :backends/libretranslate)
                 [Log$LogLevel/err]))]
         (log lvl "Response from @:\n@" url body)
         ret)))))

(defn languages [callback]
  (fetch "/languages?type=target"
         {} #(->> %
                  (map :language)
                  (map str/lower-case)
                  set
                  callback)))

(defn usage [callback]
  (if (empty? @api-key)
    (callback 0 0)
    (fetch "/usage" {} #(callback (:character_count %) (:character_limit %)))))

(defn translate [s dst callback]
  (fetch "/translate"
         {:method :post
          :body   (j/write-value-as-string
                   {:text        [s]
                    :target_lang (str/upper-case dst)})
          :filter (http/max-body-filter 1024)}
         #(callback (-> % :translations first :text))))
