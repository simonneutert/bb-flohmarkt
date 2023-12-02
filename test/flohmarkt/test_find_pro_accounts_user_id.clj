(ns flohmarkt.test-find-pro-accounts-user-id
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [flohmarkt.find-pro-accounts-user-id :refer [parse-account-id]]))

(deftest test-parse-account-id
  (testing "parsing of the account id of a dummy html content"
    (is (=
          (parse-account-id
            {:url "https://www.flohmarkt.test/p-anzeige-40896144.html",
             :dummy-body
               "ad;alkdj alskdh alsdkha sdlkjhasd posterid=40896144\";;;12313"})
          {:user-id 40896144}))))
