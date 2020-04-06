(ns retrials.listings-test
  (:require
   [cljs.test :refer-macros [is are deftest testing use-fixtures] :refer [run-tests] :as test]
   [retrials.listings :as sut]))

(def mock-trials
  [{:id 1, :name "first", :description "FOO", :archived true}
   {:id 2, :name "firstsecond", :description "BAR", :archived false}
   {:id 3, :name "third", :description "BAL", :archived false}
   {:id 4, :name "fourth", :description "KEG", :archived false}
   {:id 5, :name "fifth", :description "SPLAT", :archived true}
   {:id 6, :name "sixth", :description "LOREM", :archived false}
   {:id 7, :name "seventh", :description "IPSUM", :archived false}
   {:id 8, :name "eight", :description "FUSCE", :archived false}
   {:id 9, :name "ninth", :description "QUIS", :archived false}
   {:id 10, :name "tenth", :description "ARCU", :archived false}])

(deftest term-filter-fn-test
  (testing "returns correct amount of filtered trials"
    (is (=
          [{:id 1, :name "first", :description "FOO", :archived true}
                  {:id 2, :name "firstsecond", :description "BAR", :archived false}]

          (filter (partial sut/term-filter-fn "irst") mock-trials)))))
