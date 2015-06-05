(ns monads.writer
  "Computations that also return a vector output. Useful for
  appending log messages alongside the result of a series of
  computations."
  (:use monads.either [clojure.core.match :only [match]]))

(def eval-writer first)
(def exec-writer second)

(defn writer-tuple
  "Return a writer tuple give no value, a value, or a value
  and some output. Serves as a more explicit indication of writer
  than a simple vector pair."
  ([] [nil []])
  ([val] [val []])
  ([val output] [val output]))

(defn tell
  "Append s to the writer output."
  [s]
  [nil s])

(defn return-writer [x]
  [x []])

(defmacro writer->
  "Takes a vector of bindings and a final body. Output will
  be appended with each computation."
  [bindings body]
  (let [w0 (gensym)]
    `(let [~w0 []]
       ~(reduce (fn [acc [a b]]
                  `(let [[~a w1#] ~b
                         ~w0 (concat ~w0 w1#)]
                     ~acc))
                `(let [[result# w1#] ~body]
                   [result# (concat ~w0 w1#)])
                (reverse (partition 2 bindings))))))

(def return-writerE (comp return-writer return-either))

(defmacro writerE->
  "Like writer-> except the value should be an Either.
  Execution will stop when the first [:err ...] value is encountered."
  [bindings body]
  (let [w0 (gensym)]
    `(let [~w0 []]
       ~(reduce (fn [acc [a b]]
                  (let [v (gensym)]
                    `(let [[temp# w1#] ~b
                           ~w0 (concat ~w0 w1#)]
                       (match temp#
                         [:err dontcare#] [temp# ~w0]
                         [:ok ~v] (let [~a ~v]
                                    ~acc)))))
                `(let [[result# w1#] ~body]
                   [result# (concat ~w0 w1#)])
                (reverse (partition 2 bindings))))))

(def return-writerM return-writer)

(defmacro writerM->
  "Like writer-> except the value should be a Maybe.
  Execution will stop when the first nil value is encountered."
  [bindings body]
  (let [w0 (gensym)]
    `(let [~w0 []]
       ~(reduce (fn [acc [a b]]
                  `(let [[~a w1#] ~b
                         ~w0 (concat ~w0 w1#)]
                     (if (nil? ~a)
                       [~a ~w0]
                       ~acc)))
                `(let [[result# w1#] ~body]
                   [result# (concat ~w0 w1#)])
                (reverse (partition 2 bindings))))))
