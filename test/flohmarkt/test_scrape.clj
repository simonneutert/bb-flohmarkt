(ns flohmarkt.test-scrape
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [flohmarkt.scrape :refer
             [base-url-user create-page-url filename-cached
              url-to-filename-cached]]))

(deftest test-filename-cached
  (testing "filename-cached"
    (is (= (filename-cached "test" "edn") "./.cache/test.edn"))))

(deftest test-create-page-url
  (testing "Context of the test assertions"
    (is (= (create-page-url "https://www.flohmarkt.test/s-$i/k0"
                            (re-pattern "\\$i")
                            123)
           "https://www.flohmarkt.test/s-123/k0"))))

(deftest test-url-to-filename-cached
  (testing "Context of the test assertions"
    (is (= (url-to-filename-cached "https://www.flohmarkt.test/s-$i/k0")
           "./.cache/httpswwwflohmarkttestsik0.html"))))

(deftest test-root-url
  (testing "the root url is correctly defined"
    (is (= flohmarkt.scrape/root-url "https://www.flohmarkt.test"))))

(deftest test-base-url-user
  (testing "build the correct base url for a user id"
    (is
      (=
        (base-url-user 123)
        "https://www.flohmarkt.test/s-bestandsliste.html?userId=123&sortingField=PRICE_AMOUNT&pageNum=$i"))))