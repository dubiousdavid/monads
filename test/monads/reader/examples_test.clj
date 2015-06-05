(ns monads.reader.examples-test
  (:use monads.reader monads.either midje.sweet monads.reader.examples))

(fact ((reader-> [x (weighted1 2) y (weighted1 3)] (return-reader (+ x y))) 0.25) =>
  1.25)
(facts "readerE->"
  (fact ((readerE-> [a (weighted2 1) b (weighted2 2)] (return-readerE (+ a b))) 0.5) =>
    [:ok 1.5])
  (fact ((readerE-> [a (weighted2 2) b (weighted2 -1)] (return-readerE (+ a b))) 0.5) =>
    [:err "Negative Number!"]))
(facts "readerM->"
  (fact ((readerM-> [a (weighted3 2) b (weighted3 2)] (return-readerM (+ a b))) 0.5) =>
    2.0)
  (fact ((readerM-> [a (weighted3 2) b (weighted3 -1)] (return-readerM (+ a b))) 0.5) =>
    nil))
(fact ((readerW-> [a (weighted4 2) b (weighted4 2)] (return-readerW (+ a b))) 0.5) =>
  [2.0 (list "called with 2." "called with 2.")])
(facts "readerWE->"
  (fact ((readerWE-> [a (weighted5 3) b (weighted5 4)] (return-readerWE (+ a b))) 0.25) =>
    [[:ok 1.75] (list "called with 3." "called with 4.")])
  (fact ((readerWE-> [a (weighted5 3) b (weighted5 -1)] (return-readerWE (+ a b))) 0.25) =>
    [[:err "Negative Number!"] (list "called with 3." "called with -1.")]))
(facts "readerWM->"
  (fact ((readerWM-> [a (weighted6 3) b (weighted6 4)] (return-readerWM (+ a b))) 0.25) =>
    [1.75 (list "called with 3." "called with 4.")])
  (fact ((readerWM-> [a (weighted6 3) b (weighted6 -1)] (return-readerWM (+ a b))) 0.25) =>
    [nil (list "called with 3." "called with -1.")]))
