(ns monads.generate
  (:use example.core example.tests.midje)
  (:require monads.reader.examples monads.writer.examples
            monads.state.examples monads.either monads.maybe))

(defn- create-tests [f]
  (f 'monads.reader.examples
     :use '[monads.reader monads.either])
  (f 'monads.writer.examples
     :use '[monads.writer monads.either])
  (f 'monads.state.examples
     :use '[monads.state monads.either])
  (f 'monads.either)
  (f 'monads.maybe
     :use '[monads.either]))

(defn preview-tests []
  (create-tests gen-facts))

(defn gen-tests []
  (create-tests gen-facts-file))
