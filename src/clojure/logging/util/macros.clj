(ns logging.util.macros)

(defmacro call
  "Call a function on many many vectors at the same time"
  [f & bindings]
  `(do
     ~@(for [bind bindings]
         `(~f ~@bind))))

(defmacro name-map
  "Create a map with names of args as keys and values of args as values"
  [& syms]
  (zipmap (map keyword syms) syms))
