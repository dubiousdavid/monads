(ns monads.either-test
  (:use midje.sweet monads.either))

(facts "err?"
  (fact (err? [:err "fail"]) => true)
  (fact (err? [:ok 27]) => false))
(facts "ok?"
  (fact (ok? [:ok "Success!"]) => true)
  (fact (ok? [:err "You shall not pass!"]) => false))
(fact (return-either 39) => [:ok 39])
(facts "either"
  (fact (either identity inc [:err "fail"]) => "fail")
  (fact (either identity inc [:ok 1]) => 2))
(facts "either->maybe"
  (fact (either->maybe [:err "fail"]) => nil)
  (fact (either->maybe [:ok 1]) => 1))
(facts "either->"
  (fact (either-> [x [:err "fail"] y [:ok 1]] (return-either (+ x y))) =>
    [:err "fail"])
  (fact (either-> [x [:ok 1] y [:ok 1]] (return-either (+ x y))) => [:ok 2]))
(facts "try-either"
  (fact (try-either (/ 5 0)) => [:err [:ArithmeticException "Divide by zero"]])
  (fact (try-either (/ 10 5)) => [:ok 2]))
