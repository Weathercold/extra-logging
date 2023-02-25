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
  `(zipmap ~(map keyword syms) ~syms))

(defmacro if-let'
  "if-let with multiple bindings, short-circuiting when value evaluates to false"
  ([bindings then]
   `(if-let' ~bindings ~then nil))
  ([bindings then else]
   (if (seq bindings)
     `(if-let [~(first bindings) ~(second bindings)]
        (if-let' ~(drop 2 bindings) ~then ~else)
        ~else)
     then)))

(defmacro when-let'
  "when-let with multiple bindings, short-circuiting when value evaluates to false"
  ([bindings & body]
   (if (seq bindings)
     `(when-let [~(first bindings) ~(second bindings)]
        (when-let' ~(drop 2 bindings) ~@body))
     `(do ~@body))))
