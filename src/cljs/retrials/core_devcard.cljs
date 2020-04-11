(ns retrials.core-devcard
  (:require
   [reagent.core :as r :include-macros true]
   [retrials.listings :as listings]
   [devcards.core :as devcards :include-macros true :refer [defcard]]))

(def mock-trials
  [{:id 1, :name "first", :description "FOO", :archived true}
   {:id 2, :name "second", :description "BAR", :archived false}
   {:id 3, :name "third", :description "BAL", :archived false}
   {:id 4, :name "fourth", :description "KEG", :archived false}
   {:id 5, :name "fifth", :description "SPLAT", :archived true}
   {:id 6, :name "sixth", :description "LOREM", :archived false}
   {:id 7, :name "seventh", :description "IPSUM", :archived false}
   {:id 8, :name "eight", :description "FUSCE", :archived false}
   {:id 9, :name "ninth", :description "QUIS", :archived false}
   {:id 10, :name "tenth", :description "ARCU", :archived false}])

(defcard trials-list
  ""
  (r/as-element [listings/trials-listings mock-trials identity])
  #_{:inspect-data true
   :frame true
   :history true})

(defcard trials-list-2)
(devcards/reagent [listings/trials-listings mock-trials identity])

(defn ^:export main [] (devcards.core/start-devcard-ui!))
