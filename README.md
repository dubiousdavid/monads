# monads

Monads: Reader, Writer, State, Either, and Maybe.

Transformers: Reader+Either, Reader+Maybe, Reader+Writer, Reader+Writer+Either,
Reader+Writer+Maybe, Writer+Either, Writer+Maybe, State+Either, State+Maybe.

## Installation

```clojure
[com.2tothe8th/monads "0.2.0"]
```

## API Documentation

http://dubiousdavid.github.io/monads

## Usage

Below is a sampling of what is available with this library. There are lots more transformer combinations provided than what are shown below. Please see the API docs for more information.

### Maybe

```clojure
(use 'monads.maybe)

(maybe-> [x 1,y nil]
  (+ x y))
;; nil

(maybe-> [x 1,y 2]
  (+ x y))
;; 3

(try-maybe (/ 5 0))
;; nil

(try-maybe (/ 10 5))
;; 2
```

### Either

```clojure
(use 'monads.either)

(either-> [x [:err "fail"]
           y [:ok 1]]
  (return-either (+ x y)))
;; [:err "fail"]

(either-> [x [:ok 1]
           y [:ok 1]]
  (return-either (+ x y)))
;; [:ok 2]

(try-either (/ 5 0))
;; [:err [:ArithmeticException "Divide by zero"]]

(try-either (/ 10 5))
;; [:ok 2]
```

### Reader

```clojure
(use 'monads.reader)

(defn weighted1 [x]
  (fn [weight]
    (* x weight)))

((reader-> [x (weighted1 2)
            y (weighted1 3)]
   (return-reader (+ x y)))
 0.25)
;; 1.25

(defn weighted2 [x]
  (fn [weight]
    (if (< x 0)
      [:err "Negative Number!"]
      [:ok (* x weight)])))

((readerE-> [a (weighted2 1)
             b (weighted2 2)]
   (return-readerE (+ a b)))
 0.50)
;; [:ok 1.5]
```

### State

```clojure
(use 'monads.state)

(defn push-stack [x]
  (fn [state]
    [nil (conj state x)]))

(defn pop-stack []
  (fn [state]
    [(peek state) (pop state)]))

((state-> [_ (chain-state (push-stack 1) (push-stack 2))
           a (pop-stack)
           _ (push-stack 3)
           b (pop-stack)]
   (return-state (+ a b)))
 [])
;; [5 [1]]
```

### Writer

```clojure
(use 'monads.writer)

(defn plus-one [x]
  [(inc x) [(str "You passed " x ".")]])

(writer-> [a (plus-one 2)
           b (plus-one 3)
           _ (tell ["Add the two numbers."])]
  (return-writer (+ a b)))
;; [7 ("You passed 2." "You passed 3." "Add the two numbers.")]

(defn weighted2 [x]
  (let [log [(str "called with " x ".")]]
    (if (< x 0)
      [nil log]
      [(* x 0.25) log])))

(writerM-> [a (weighted2 2)
            b (weighted2 -1)]
  (return-writerM (+ a b)))
;; [nil ("called with 2." "called with -1.")]
```

## Tests

Run `lein midje`
