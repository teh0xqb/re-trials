(ns retrials.lib
  (:require [reagent.core :as r]
            [cljs-http.client :as client]
            [cljs.core.async :as async]
            [cljs.pprint :refer [pprint]]

            ["@material-ui/core/Table" :default Table]
            ["@material-ui/core/TableCell" :default TableCell]
            ["@material-ui/core/TableContainer" :default TableContainer]
            ["@material-ui/core/TableHead" :default TableHead]
            ["@material-ui/core/TableRow" :default TableRow]
            ["@material-ui/core/TableBody" :default TableBody]))


(defonce global-store (r/atom []))

;; hydrate the store (init)
;; could be renamed to fetch
(defn hydrate [] (async/go
                   (let [response (async/<! (client/get "https://5dc26490.ngrok.io/trials"))]
                     (println response)
                     (when (:success response)
                       (reset! global-store (:body response))))))

;; dev only
(add-watch global-store :global-store-watcher (fn [key atom old-state new-state]
                                     (pprint new-state)))

(def columns ["Name" "Description" "Archived?"])

(defn trials-listings []
  (let [trials @global-store]
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
         [:> TableRow
          [:> TableCell (:name trial)]
          [:> TableCell (:description trial)]
          [:> TableCell (str (:archived trial))]])]]]))


(defn edit-trial! [{:keys [id name]}]
  (client/post (str "https://5dc26490.ngrok.io/trial/" id) {:json-params {:name name}}))

(defn edit-form []
  (let [id (r/atom "") ;; swap these atoms on-change below
        name (r/atom "")]
    ;; TODO Define handler for button to call edit-trial!
    ;; TODO handle async -> when 200, show success and refresh the store. when error, display err string
    (fn []
      [:div

       [:h3 "Indicate trial fields to edit" ]

       [:label {:for "trial-id"} "id"]
       [:input {:id "trial-id" :value @id}] ;; needs on change

       [:br]

       [:label {:for "trial-name"} "name"]
       [:input {:id "trial-name" :value @name}] ;; needs on change to be fully controlled

       [:br]
       [:button {:on-click identity} "Edit Trial"]

       ])))

;; (comment "edit-trial!: run post call with id param above")

(defn root-component []
  [:div
   [:p "Hello World"]
   [trials-listings]

   [edit-form]])

(defn mount-tree
  "mount root node for reagent tree"
  []
  (r/render [root-component] (js/document.getElementById "app")))


;; REPL'ing ======================

;; Doing a POST with cljs-http
(comment (def answer (client/post "https://5dc26490.ngrok.io/trial/1" {:json-params {:name "No-AFTS-Here"}}))
         (async/go (let [res (async/<! answer)]
                     (println res))))
