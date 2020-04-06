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


(defn term-filter-fn [name-term item]
  (not (empty?
        (re-find
         (re-pattern (str ".*" name-term ".*"))
         (:name item)))))

(def ^:const columns ["Name" "Description" "Archived?"])

(defn trials-listings [trials on-save-trial]
  {:pre  [(fn? on-save-trial)
          (vector? trials)]}
  (let [selected-trial (r/atom nil)
        filter-term (r/atom "")]
    (fn [trials on-save-trial]
      [:> Paper

       [editing/atom-input
        {:label "Filter by name: "
         :style {:width "30%"}}
        filter-term]

       [:> TableContainer
        [:> Table
         [:> TableHead
          [:> TableRow
           (for [column columns]
             ^{:key column}
             [:> TableCell column])]]

         [:> TableBody
          (if-not (empty? trials)
            (for [trial (filter (partial term-filter-fn @filter-term) trials)]
              ^{:key (:id trial)}
              [:> TableRow {:hover true
                            :style {:cursor "pointer"}
                            :on-click #(reset! selected-trial trial)}
               [:> TableCell (:name trial)]
               [:> TableCell (:description trial)]
               [:> TableCell
                [:input {:type "checkbox"
                         :disabled true
                         :checked (boolean (:archived trial))}]]])

            [:> TableRow
             [:> TableCell
              {:align "center" :colspan 3}
              "No trials available."]])]]]

       (when-let [trial-now @selected-trial]
         [editing/trial-dialog
          {:trial trial-now
           :on-submit on-save-trial
           :on-close #(reset! selected-trial nil)}])])))
