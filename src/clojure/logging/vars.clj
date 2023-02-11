(ns logging.vars)

(def is-foo
  "Whether we're running on foo's client."
  (delay
   (try (Class/forName "mindustry.client.ClientVars")
        true
        (catch Exception _ false))))
