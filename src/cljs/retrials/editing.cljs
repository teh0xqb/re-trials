(ns retrials.editing
  (:require
   [reagent.core :as r]
   ["@material-ui/core/TextField" :default TextField]
   ["@material-ui/core/Dialog" :default Dialog]
   ["@material-ui/core/DialogTitle" :default DialogTitle]
   ["@material-ui/core/Button" :default Button]
   ["@material-ui/core/Snackbar" :default Snackbar]
   ["@material-ui/icons/HighlightOff" :default HighlightIcon]
   ["@material-ui/core/IconButton" :default IconButton]
   ))

(defn atom-input
  "Ref-atom-aware input element, with on-change handler for ref-value prop."
  [props ref-value]
  [:> TextField (merge props
                       {:type "text"
                        :value @ref-value
                        :variant "outlined"
                        :on-change #(reset! ref-value (-> % .-target .-value))})])

(defn edit-form [trial on-submit]
  {:pre  [(fn? on-submit)
          (map? trial)]} ;; experimenting with eq of "prop types"; could be disabled in prod with `:elide-asserts true`
  (let [description (r/atom (:description trial))
        name (r/atom (:name trial))]
    ;; TODO handle async -> when 200, show success and refresh the store. when error, display err string
    (fn [] ;; no need to repeat args as we don't refresh while editing
      [:div
       {:style {:padding "1em" :display "flex" :flex-direction "column"}}

       [atom-input {:label "Name"} name] [:br]

       [atom-input {:label "Description"} description] [:br]

       [:> Button
        {:on-click (fn [_] (on-submit (merge trial {:description @description :name @name})))
         :variant "contained"}
        "Save"]])))

(defn trial-dialog
  ""
  [{:keys [trial on-close on-submit]}]
  [:> Dialog
   {:aria-labelledby "Trial Edit Dialog"
    :on-close on-close
    :max-width "sm"
    :full-width true
    :open true}

   [:> IconButton
    {:style
     {:position "absolute"
      :top 10
      :right 10}
     :on-click on-close}
    [:> HighlightIcon]]

   [:> DialogTitle (str "Edit Trial" " \"" (:name trial) "\"")]

   [edit-form trial on-submit]])
