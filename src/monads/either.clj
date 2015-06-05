(ns monads.either
  "Represents values that are either an error or a valid value."
  (:use example.core [clojure.core.match :only [match]]))

(defn err? [x]
  (-> x first (= :err)))

(defn ok? [x]
  (-> x first (= :ok)))

(defn return-either [x]
  [:ok x])

(ex "err?"
    (err? [:err "fail"])
    (err? [:ok 27]))

(ex "ok?"
    (ok? [:ok "Success!"])
    (ok? [:err "You shall not pass!"]))

(ex (return-either 39))

(def unwrap-either second)

(defn either
  "Call err-fn if either is [:err ...], otherwise call ok-fn."
  [err-fn ok-fn e]
  (match e
    [:ok v] (ok-fn v)
    [:err v] (err-fn v)))

(ex "either"
    (either identity inc [:err "fail"])
    (either identity inc [:ok 1]))

(defn either->maybe
  "If the either value is [:err ...], return nil, otherwise return
  the [:ok ...] value unwrapped."
  [e]
  (match e
    [:ok v] v
    [:err _] nil))

(ex "either->maybe"
    (either->maybe [:err "fail"])
    (either->maybe [:ok 1]))

(defmacro either->
  "Takes a vector of bindings and a final body. Terminates
  execution when the first [:err ...] value is encountered,
  otherwise returns the body."
  [bindings body]
  (reduce (fn [acc [a b]]
            (let [v (gensym)]
              `(let [temp# ~b]
                 (match temp#
                   [:err dontcare#] temp#
                   [:ok ~v] (let [~a ~v]
                              ~acc)))))
          body
          (reverse (partition 2 bindings))))

(ex "either->"
    (either-> [x [:err "fail"]
               y [:ok 1]]
      (return-either (+ x y)))

    (either-> [x [:ok 1]
               y [:ok 1]]
      (return-either (+ x y))))

(defmacro try-either
  "Execute body within a try/catch block. Returns the
  body wrapped in an [:ok ...] if no exception is thrown,
  otherwise, the exception wrapped in an [:err ...]."
  [body]
  `(try
     [:ok ~body]
     (catch Exception e#
       [:err [(-> e# class .getSimpleName keyword) (.getMessage e#)]])))

(ex "try-either"
    (try-either (/ 5 0))
    (try-either (/ 10 5)))
