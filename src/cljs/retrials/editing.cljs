(ns retrials.editing
  (:require
   [reagent.core :as r]
   [reagent.impl.template :as rtpl]
   [cljs.core.async :as async]
   ["@material-ui/core/Dialog" :default Dialog]
   ["@material-ui/core/DialogTitle" :default DialogTitle]
   ["@material-ui/core/Button" :default Button]
   ["@material-ui/core/Snackbar" :default Snackbar]
   ["@material-ui/icons/HighlightOff" :default HighlightIcon]
   ["@material-ui/core/TextField" :default TextField]
   ["@material-ui/core/Checkbox" :default Checkbox]
   ["@material-ui/core/FormControlLabel" :default FormControlLabel]
   ["@material-ui/core/IconButton" :default IconButton]
   ["@material-ui/core/CircularProgress" :default Loading]))

(def mui-compat-input
  (r/reactify-component
   (fn [props]
     [:input (-> props
                 (assoc :ref (:inputRef props))
                 (dissoc :inputRef))])))

(def ^:private textarea-component
  (r/reactify-component
   (fn [props]
     [:textarea (-> props
                    (assoc :ref (:inputRef props))
                    (dissoc :inputRef))])))

;; To fix cursor jumping when controlled input value is changed,
;; use wrapper input element created by Reagent instead of
;; letting Material-UI to create input element directly using React.
;; Create-element + convert-props-value is the same as what adapt-react-class does.
(defn text-field [props & children]
  (let [props (-> props
                  (assoc-in [:InputProps :inputComponent] (cond
                                                            (and (:multiline props) (:rows props) (not (:maxRows props)))
                                                            textarea-component

                                                            ;; FIXME: Autosize multiline field is broken.
                                                            (:multiline props)
                                                            nil

                                                            ;; Select doesn't require cursor fix so default can be used.
                                                            (:select props)
                                                            nil

                                                            :else
                                                            mui-compat-input))
                  rtpl/convert-prop-value)]
    (apply r/create-element TextField props (map r/as-element children))))

(defn atom-input
  "Ref-atom-aware input element.
   TODO: in the future I'd probably reconsider having edit-form track editing state
   in one map, and wouldn't have a need for an atom-aware input element."
  [props ref-value]
  [text-field
   (merge {:value @ref-value ;; or r/merge-props   !!
           :variant "outlined"
           :on-change #(reset! ref-value (-> % .-target .-value))}
          props)])

(defn atom-checkbox
  "Atom-aware checkbox"
  [props ref-value]
  [:> FormControlLabel
   (merge {:control (r/as-element
                     [:div
                      [:> Checkbox
                       {:checked @ref-value
                        :on-change #(swap! ref-value not)}]])}
          props)])

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

       [atom-input {:label "Description"
                    :multiline true
                    :rows 2
                    :size "medium"} description] [:br]

       ;; for requirement of implementing edit for two different types
       [atom-checkbox {:label "Archived"} archived?] [:br]

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
