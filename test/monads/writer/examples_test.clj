(ns monads.writer.examples-test
  (:use monads.writer monads.either midje.sweet monads.writer.examples))

(fact (writer-> [a (plus-one 2) b (plus-one 3) _ (tell ["Add the two numbers."])] (return-writer (+ a b))) =>
  [7 (list "You passed 2." "You passed 3." "Add the two numbers.")])
(facts "writerE->"
  (fact (writerE-> [a (weighted1 2) b (weighted1 3)] (return-writerE (+ a b))) =>
    [[:ok 1.25] (list "called with 2." "called with 3.")])
  (fact (writerE-> [a (weighted1 2) b (weighted1 -1)] (return-writerE (+ a b))) =>
    [[:err "Negative number!"] (list "called with 2." "called with -1.")]))
(facts "writerM->"
  (fact (writerM-> [a (weighted2 2) b (weighted2 3)] (return-writerM (+ a b))) =>
    [1.25 (list "called with 2." "called with 3.")])
  (fact (writerM-> [a (weighted2 2) b (weighted2 -1)] (return-writerM (+ a b))) =>
    [nil (list "called with 2." "called with -1.")]))
