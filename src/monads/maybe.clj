(ns monads.maybe
  "Represents values that might by empty (i.e., nil)."
  (:use example.core monads.either
        [clojure.core.match :only [match]]))

(defn maybe
  "If the value is nil return default, otherwise call f on the
  maybe value."
  [default f m]
  (if (nil? m) default (f m)))

(ex "maybe"
    (maybe 1 inc 2)
    (maybe 1 inc nil))

(defn maybe->either
  "If the maybe value is nil return default wrapped in Err,
  otherwise return the value wrapped in Ok."
  [default m]
  (match m
    nil [:err default]
    _ [:ok m]))

(ex "maybe->either"
    (maybe->either 1 2)
    (maybe->either 1 nil))

(defmacro maybe->
  "Takes a vector of bindings and a final body. Terminates
  execution when the first nil value is encountered,
  otherwise returns the then value.  Optionally, pass an
  else value that will be returned if a nil value is encountered
  in the bindings."
  ([bindings then]
     (reduce (fn [acc [a b]]
               `(when-let [~a ~b] ~acc))
             then
             (reverse (partition 2 bindings))))
  ([bindings then else]
     (reduce (fn [acc [a b]]
               `(if-let [~a ~b] ~acc ~else))
             then
             (reverse (partition 2 bindings)))))

(ex "maybe->"
    (maybe-> [x 1,y nil]
      (+ x y))

    (maybe-> [x 1,y 2]
      (+ x y))

    (maybe-> [x nil,y 2]
      (+ x y)
      "Fail")

    (maybe-> [x 1,y nil]
      (+ x y)
      "Fail")

    (maybe-> [x 1,y 2]
      (+ x y)
      "Fail"))

(defmacro try-maybe
  "Execute body within a try/catch block. Returns the
  body if no exception is thrown, otherwise nil."
  [body]
  `(try
     ~body
     (catch Exception e# nil)))

(ex "try-maybe"
    (try-maybe (/ 5 0))
    (try-maybe (/ 10 5)))
