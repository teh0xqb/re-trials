(ns retrials.listings
  (:require
   [reagent.core :as r]
   [retrials.editing :as editing]
   ["@material-ui/core/Paper" :default Paper]
   ["@material-ui/core/TableContainer" :default TableContainer]
   ["@material-ui/core/Table" :default Table]
   ["@material-ui/core/TableCell" :default TableCell]
   ["@material-ui/core/TableHead" :default TableHead]
   ["@material-ui/core/TableRow" :default TableRow]
   ["@material-ui/core/TableBody" :default TableBody]))


(def ^:const columns ["Name" "Description" "Archived?"])

(defn trials-listings [trials on-save-trial]
  (let [selected-trial (r/atom nil)]
    (fn [trials on-save-trial]
      [:> Paper
       [:> TableContainer
        [:> Table
         [:> TableHead
          [:> TableRow
           (for [column columns]
             ^{:key column}
             [:> TableCell column])]]
         [:> TableBody
          (for [trial trials]
            ^{:key (:id trial)}
            [:> TableRow {:hover true
                          :style {:cursor "pointer"}
                          :on-click #(reset! selected-trial trial)}
             [:> TableCell (:name trial)]
             [:> TableCell (:description trial)]
             [:> TableCell
              [:input {:type "checkbox"
                       :disabled true
                       :checked (boolean (:archived trial))}]]])]]]

       (when-let [trial-now @selected-trial]
         [editing/trial-dialog
          {:trial trial-now
           :on-submit on-save-trial
           :on-close #(reset! selected-trial nil)}])])))
