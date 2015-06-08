(ns monads.state
  "Pass an updatable state through a computation."
  (:use example.core monads.either
        [clojure.core.match :only [match]]))

(defn state-tuple
  "Returns a state tuple give no value, a value, or a value
  and some state. Serves as a more explicit indication of state
  than a simple vector pair."
  ([] [nil nil])
  ([state] [nil state])
  ([val state] [val state]))

(defn ask
  "Get the current state."
  []
  (fn [state] [state state]))

(defn asks
  "Apply f to the current state, and return the result.
  State is unmodified."
  [f]
  (fn [state] [(f state) state]))

(defn set-state
  "Overwrite the current state."
  [state]
  (fn [_] [nil state]))

(defn update-state
  "Apply f and args to the current state."
  ([f]
   (fn [state] [nil (f state)]))
  ([f & args]
   (fn [state] [nil (apply f state args)])))

(defn return-state
  "Wrap a value in a state context."
  [v]
  (fn [state] [v state]))

(comment
  (fn [state]
    (let [[user-id state] ((add-user "Mr." "T") state)]
      (let [[user state] ((get-user user-id) state)]
        ((return-state user) state)))))

(defmacro state->
  "Takes a vector of bindings and a final body.  State will
  be passed to each value on the righthand side of the
  bindings vector, and to the final body value."
  [bindings body]
  (let [state (gensym)]
    `(fn [~state]
       ~(reduce (fn [acc [a b]]
                  `(let [[~a ~state] (~b ~state)] ~acc))
                `(~body ~state)
                (reverse (partition 2 bindings))))))

(defmacro chain-state
  "Chain multiple stateful computations together, discarding
  the value along the way."
  [& args]
  `(state-> [~@(mapcat (fn [form] ['_ form]) (butlast args))]
     ~(last args)))

(comment
  (chain-state (set-state 1) (update-state inc) (get-state)))

(defn run-state
  "Run a stateful computation with init-state.
  Returns the final value/state pair."
  [state-fn init-state]
  (state-fn init-state))

(defn eval-state
  "Run a stateful computation with init-state.
  Returns the final value sans the final state."
  [state-fn init-state]
  (first (state-fn init-state)))

(defn exec-state
  "Run a stateful computation with init-state.
  Returns the final state."
  [state-fn init-state]
  (second (state-fn init-state)))

(defn inspect
  "Place anywhere in a set of routes to have the current
  state printed.  Has no effect otherwise."
  [state]
  (println state)
  [nil state])

(defmacro statefn
  "Create a state function. Automatically propagates state.
  Useful if you only want to read the state and not modify
  it."
  [bindings & body]
  (let [state-binding (first bindings)
        state-binding (if (= state-binding '_)
                        (gensym)
                        state-binding)]
    `(fn [~state-binding]
       [(do ~@body) ~state-binding])))

(def return-stateE (comp return-state return-either))

(defmacro stateE->
  "Like state-> except the value should be an Either.
  Execution will stop when the first [:err ...] value is encountered."
  [bindings body]
  (let [state (gensym)]
    `(fn [~state]
       ~(reduce (fn [acc [a b]]
                  (let [v (gensym)]
                    `(let [[temp# ~state] (~b ~state)]
                       (match temp#
                         [:err dontcare#] [temp# ~state]
                         [:ok ~v] (let [~a ~v]
                                    ~acc)))))
                `(~body ~state)
                (reverse (partition 2 bindings))))))

(defn wrap-state-either
  "Takes a state fn and creates a new state fn where the
  value returned is wrapped in an [:ok ...]."
  [state-fn]
  (fn [state0]
    (let [[val state1] (state-fn state0)]
      [(return-either val) state1])))

(def return-stateM return-state)

(defmacro stateM->
  "Like state-> except the value should be a Maybe.
  Execution will stop when the first nil value is encountered."
  [bindings body]
  (let [state (gensym)]
    `(fn [~state]
       ~(reduce (fn [acc [a b]]
                  `(let [[~a ~state] (~b ~state)]
                     (if (nil? ~a)
                       [~a ~state]
                       ~acc)))
                `(~body ~state)
                (reverse (partition 2 bindings))))))
