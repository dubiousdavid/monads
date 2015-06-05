(ns monads.writer.examples
  (:use example.core [monads reader writer]))

(defn plus-one [x]
  [(inc x) [(str "You passed " x ".")]])

(ex (writer-> [a (plus-one 2)
               b (plus-one 3)
               _ (tell ["Add the two numbers."])]
      (return-writer (+ a b))))

(defn weighted1 [x]
  (let [log [(str "called with " x ".")]]
    (if (< x 0)
      [[:err "Negative number!"] log]
      [[:ok (* x 0.25)] log])))

(ex "writerE->"
    (writerE-> [a (weighted1 2)
                b (weighted1 3)]
      (return-writerE (+ a b)))

    (writerE-> [a (weighted1 2)
                b (weighted1 -1)]
      (return-writerE (+ a b))))

(defn weighted2 [x]
  (let [log [(str "called with " x ".")]]
    (if (< x 0)
      [nil log]
      [(* x 0.25) log])))

(ex "writerM->"
    (writerM-> [a (weighted2 2)
                b (weighted2 3)]
      (return-writerM (+ a b)))

    (writerM-> [a (weighted2 2)
                b (weighted2 -1)]
      (return-writerM (+ a b))))
