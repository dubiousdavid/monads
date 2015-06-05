(ns monads.reader.examples
  (:use example.core monads.reader))

(defn weighted1 [x]
  (fn [weight]
    (* x weight)))

(ex ((reader-> [x (weighted1 2)
                y (weighted1 3)]
       (return-reader (+ x y)))
     0.25))

(defn weighted2 [x]
  (fn [weight]
    (if (< x 0)
      [:err "Negative Number!"]
      [:ok (* x weight)])))

(ex "readerE->"
    ((readerE-> [a (weighted2 1)
                 b (weighted2 2)]
       (return-readerE (+ a b)))
     0.50)

    ((readerE-> [a (weighted2 2)
                 b (weighted2 -1)]
       (return-readerE (+ a b)))
     0.50))

(defn weighted3 [x]
  (fn [weight]
    (if (< x 0)
      nil
      (* x weight))))

(ex "readerM->"
    ((readerM-> [a (weighted3 2)
                 b (weighted3 2)]
       (return-readerM (+ a b)))
     0.50)

    ((readerM-> [a (weighted3 2)
                 b (weighted3 -1)]
       (return-readerM (+ a b)))
     0.50))

(defn weighted4 [x]
  (fn [weight]
    (let [log [(str "called with " x ".")]]
      [(* x weight) log])))

(ex ((readerW-> [a (weighted4 2)
                 b (weighted4 2)]
       (return-readerW (+ a b)))
     0.50))

(defn weighted5 [x]
  (fn [weight]
    (let [log [(str "called with " x ".")]]
      (if (< x 0)
        [[:err "Negative Number!"] log]
        [[:ok (* x weight)] log]))))

(ex "readerWE->"
    ((readerWE-> [a (weighted5 3)
                  b (weighted5 4)]
       (return-readerWE (+ a b)))
     0.25)

    ((readerWE-> [a (weighted5 3)
                  b (weighted5 -1)]
       (return-readerWE (+ a b)))
     0.25))

(defn weighted6 [x]
  (fn [weight]
    (let [log [(str "called with " x ".")]]
      (if (< x 0)
        [nil log]
        [(* x weight) log]))))

(ex "readerWM->"
    ((readerWM-> [a (weighted6 3)
                  b (weighted6 4)]
                 (return-readerWM (+ a b)))
     0.25)

    ((readerWM-> [a (weighted6 3)
                  b (weighted6 -1)]
                 (return-readerWM (+ a b)))
     0.25))
