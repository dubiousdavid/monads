(ns monads.maybe-test
  (:use monads.either midje.sweet monads.maybe))

(facts "maybe"
  (fact (maybe 1 inc 2) => 3)
  (fact (maybe 1 inc nil) => 1))
(facts "maybe->either"
  (fact (maybe->either 1 2) => [:ok 2])
  (fact (maybe->either 1 nil) => [:err 1]))
(facts "maybe->"
  (fact (maybe-> [x 1 y nil] (+ x y)) => nil)
  (fact (maybe-> [x 1 y 2] (+ x y)) => 3)
  (fact (maybe-> [x nil y 2] (+ x y) "Fail") => "Fail")
  (fact (maybe-> [x 1 y nil] (+ x y) "Fail") => "Fail")
  (fact (maybe-> [x 1 y 2] (+ x y) "Fail") => 3))
(facts "try-maybe"
  (fact (try-maybe (/ 5 0)) => nil)
  (fact (try-maybe (/ 10 5)) => 2))
