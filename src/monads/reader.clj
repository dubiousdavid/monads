(ns monads.reader
  "Computations that read from a static environment."
  (:use [monads either writer]
        [clojure.core.match :only [match]]))

(defn return-reader [x]
  (fn [_] x))

(defmacro reader->
  "Takes a vector of bindings and a final body. Environment will
  be passed to each value on the righthand side of the
  bindings vector, and to the final body value."
  [bindings body]
  (let [env (gensym)]
    `(fn [~env]
       ~(reduce (fn [acc [a b]]
                  `(let [~a (~b ~env)] ~acc))
                `(~body ~env)
                (reverse (partition 2 bindings))))))

(defn ask []
  (fn [env] env))

(defn asks [f]
  (fn [env] (f env)))

(defn run-reader [reader-fn env]
  (reader-fn env))

(def return-readerE (comp return-reader return-either))

(defmacro readerE->
  "Like reader-> except the value should be an Either.
  Execution will stop when the first [:err ...] value is encountered."
  [bindings body]
  (let [env (gensym)]
    `(fn [~env]
       ~(reduce (fn [acc [a b]]
                  (let [v (gensym)]
                    `(let [temp# (~b ~env)]
                       (match temp#
                         [:err dontcare#] temp#
                         [:ok ~v] (let [~a ~v]
                                    ~acc)))))
                `(~body ~env)
                (reverse (partition 2 bindings))))))

(def exec-readerW
  (comp exec-writer run-reader))

(def eval-readerW
  (comp eval-writer run-reader))

(def return-readerM return-reader)

(defmacro readerM->
  "Like reader-> except the value should be a Maybe.
  Execution will stop when the first nil value is encountered."
  [bindings body]
  (let [env (gensym)]
    `(fn [~env]
       ~(reduce (fn [acc [a b]]
                  `(let [~a (~b ~env)]
                     (if (nil? ~a)
                       ~a
                       ~acc)))
                `(~body ~env)
                (reverse (partition 2 bindings))))))

(def return-readerW
  (comp return-reader return-writer))

(defmacro readerW->
  "Like reader-> except that output is appended."
  [bindings body]
  (let [env (gensym)
        w0 (gensym)]
    `(let [~w0 []]
       (fn [~env]
         ~(reduce (fn [acc [a b]]
                    `(let [[~a w1#] (~b ~env)
                           ~w0 (concat ~w0 w1#)]
                       ~acc))
                  `(let [[result# w1#] (~body ~env)]
                     [result# (concat ~w0 w1#)])
                  (reverse (partition 2 bindings)))))))

(def return-readerWE
  (comp return-reader return-writer return-either))

(defmacro readerWE->
  "Like reader-> except the value should be an Either
  and output is appended. Execution will stop when the
  first [:err ...] value is encountered."
  [bindings body]
  (let [env (gensym)
        w0 (gensym)]
    `(let [~w0 []]
       (fn [~env]
         ~(reduce (fn [acc [a b]]
                    (let [v (gensym)]
                      `(let [[temp# w1#] (~b ~env)
                             ~w0 (concat ~w0 w1#)]
                         (match temp#
                           [:err dontcare#] [temp# ~w0]
                           [:ok ~v] (let [~a ~v]
                                      ~acc)))))
                  `(let [[result# w1#] (~body ~env)]
                     [result# (concat ~w0 w1#)])
                  (reverse (partition 2 bindings)))))))

(def return-readerWM
  (comp return-reader return-writer))

(defmacro readerWM->
  "Like reader-> except the value should be a Maybe
  and output is appended. Execution will stop when the
  first nil value is encountered."
  [bindings body]
  (let [env (gensym)
        w0 (gensym)]
    `(let [~w0 []]
       (fn [~env]
         ~(reduce (fn [acc [a b]]
                    `(let [[~a w1#] (~b ~env)
                           ~w0 (concat ~w0 w1#)]
                       (if (nil? ~a)
                         [~a ~w0]
                         ~acc)))
                  `(let [[result# w1#] (~body ~env)]
                     [result# (concat ~w0 w1#)])
                  (reverse (partition 2 bindings)))))))
