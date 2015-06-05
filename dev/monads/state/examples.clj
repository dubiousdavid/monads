(ns monads.state.examples
  (:use example.core monads.state))

(defn weighted1 [x]
  (statefn [weight]
    (if (< x 0)
      [:err "Negative Number!"]
      [:ok (* x weight)])))

(ex "stateE->"
    (-> (stateE-> [a (weighted1 1)
                   b (weighted1 2)]
          (return-stateE (+ a b)))
        (eval-state 0.50))

    (-> (stateE-> [a (weighted1 2)
                   b (weighted1 -1)]
          (return-stateE (+ a b)))
        (eval-state 0.50)))

(defn push-stack [x]
  (fn [state]
    [nil (conj state x)]))

(defn pop-stack []
  (fn [state]
    [(peek state) (pop state)]))

(ex ((state-> [_ (chain-state (push-stack 1) (push-stack 2))
               a (pop-stack)
               _ (push-stack 3)
               b (pop-stack)]
       (return-state (+ a b)))
     []))
