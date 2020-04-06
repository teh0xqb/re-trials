(ns retrials.editing
  (:require
   [reagent.core :as r]
   [cljs.core.async :as async]
   ["@material-ui/core/Dialog" :default Dialog]
   ["@material-ui/core/DialogTitle" :default DialogTitle]
   ["@material-ui/core/Button" :default Button]
   ["@material-ui/core/Snackbar" :default Snackbar]
   ["@material-ui/icons/HighlightOff" :default HighlightIcon]
   ["@material-ui/core/IconButton" :default IconButton]
   ["@material-ui/core/CircularProgress" :default Loading]))

(defn atom-input
  "Ref-atom-aware input element.
   TODO: in the future I'd probably reconsider having edit-form track editing state
   in one map, and wouldn't have a need for an atom-aware input element."
  [props ref-value]
  (let [id (gensym "input")]
    [:<>
     [:label {:for id} (:label props)]
     [:input
      (merge props
             {:type "text"
              :id id
              :value @ref-value
              :on-change #(reset! ref-value (-> % .-target .-value))})]]))

(defn atom-checkbox
  "Atom-aware checkbox"
  [props ref-value]
  [:input {:type "checkbox"
           :on-change #(swap! ref-value not)
           :checked @ref-value}])

(defn error-banner [error-text]
  [:p {:style
       {:color "red"
        :margin "0 0 1em 0"}}
   error-text])

(defn edit-form [trial on-submit on-success]
  {:pre  [(fn? on-submit)
          (map? trial)]}
  (let [description (r/atom (:description trial)) ;; replace with editing map for all fields later
        name (r/atom (:name trial))
        archived? (r/atom (:archived trial))
        ;; loading, error, and handler can be encapsulated/cleanup later.
        loading (r/atom false)
        error (r/atom nil)
        save-handler (fn [_]
                       (reset! loading true)
                       (let [request (on-submit
                                  (merge trial
                                         {:description @description
                                          :name @name
                                          :archived @archived?}))]
                         (async/go
                           (let [response (async/<! request)]
                             (reset! loading false)
                             (when (not (nil? response))
                               (if (:success response)
                                 (on-success)
                                 (reset! error (:msg response))))))))]
    (fn [] ;; skip props: no need to refresh args while editing.
      [:div
       {:style {:padding "1.2em"
                :display "flex"
                :flex-direction "column"}}

       [atom-input {:label "Name"} name] [:br]

       [atom-input {:label "Description"} description] [:br]

       ;; for requirement of implementing edit for two different types
       [:label "Archived?"
        [atom-checkbox {:id "archived" :type "checkbox"} archived?]] [:br]

       (when-let [error-instance @error] [error-banner error-instance])

       [:> Button
        {:disabled @loading
         :on-click save-handler
         :variant "contained"}
        (if @loading [:> Loading] "Save")]])))

(defn trial-dialog
  "Simple dialog used to display and edit a trial's name and description"
  [{:keys [trial on-close on-submit]}]
  {:pre [(map? trial)
         (fn? on-close)
         (fn? on-submit)]}
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

   [:> DialogTitle
    (str "Edit Trial" " \"" (:name trial) "\"")]

   [edit-form trial on-submit on-close]])
