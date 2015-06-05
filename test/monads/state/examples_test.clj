(ns monads.state.examples-test
  (:use monads.state monads.either midje.sweet monads.state.examples))

(facts "stateE->"
  (fact (-> (stateE-> [a (weighted1 1) b (weighted1 2)] (return-stateE (+ a b))) (eval-state 0.5)) =>
    [:ok 1.5])
  (fact (-> (stateE-> [a (weighted1 2) b (weighted1 -1)] (return-stateE (+ a b))) (eval-state 0.5)) =>
    [:err "Negative Number!"]))
(fact ((state-> [_ (chain-state (push-stack 1) (push-stack 2)) a (pop-stack) _ (push-stack 3) b (pop-stack)] (return-state (+ a b))) []) =>
  [5 [1]])
