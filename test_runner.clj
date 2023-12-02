#!/usr/bin/env bb

;; SOURCE: https://book.babashka.org/#_running_tests
(require '[clojure.test :as t] '[babashka.classpath :as cp])

(cp/add-classpath "src:test")

(require 'flohmarkt.test-scrape 'flohmarkt.test-find-pro-accounts-user-id)

(def test-results
  (t/run-tests 'flohmarkt.test-scrape
               'flohmarkt.test-find-pro-accounts-user-id))

(let [{:keys [fail error]} test-results]
  (when (pos? (+ fail error)) (System/exit 1)))