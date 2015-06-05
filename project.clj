(defproject com.2tothe8th/monads "0.1.0"
  :description "Reader, Writer, State, Either, and Maybe monads."
  :url "https://github.com/dubiousdavid/monads"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.2tothe8th/example "0.3.0"]
                 [org.clojure/core.match "0.2.2"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]
                   :source-paths ["dev"]}}
  :codox {:src-dir-uri "https://github.com/dubiousdavid/monads/blob/master"
          :src-linenum-anchor-prefix "L"
          :exclude [monads.reader.examples
                    monads.writer.examples
                    monads.state.examples
                    monads.generate]
          :output-dir "."})
