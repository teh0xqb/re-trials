(ns retrials.lib
  (:require [reagent.core :as r]
            [cljs-http.client :as client]
            [cljs.core.async :as async]
            [cljs.pprint :refer [pprint]]

            [retrials.app-bar :refer [app-bar]]
            [retrials.listings :refer [trials-listings]]

            ["@material-ui/core/Grid" :default Grid]
            ["@material-ui/core/Typography" :default Typography]
            ["@material-ui/core/styles" :refer [withStyles]]))


(defonce global-store (r/atom [{:id 1, :name "No-AFTS-Here", :description "FOO", :archived true}
                               {:id 2, :name "second", :description "BAR", :archived false} ]))

;; TODO mock the store
;; TODO define config for urls and constants

;; hydrate the store (init)
;; could be renamed to fetch
(defn hydrate [] nil #_(async/go
                   (let [response (async/<! (client/get "https://5dc26490.ngrok.io/trials"))]
                     (println response)
                     (when (:success response)
                       (reset! global-store (:body response))))))

;; dev only
(add-watch global-store :global-store-watcher (fn [key atom old-state new-state]
                                     (pprint new-state)))


(defn edit-trial!
  "Received complete or partial info of a trial, and edits it on the server."
  [{:keys [id] :as trial}]
  (println "submiting trial " trial)
  (client/post (str "https://5dc26490.ngrok.io/trial/" id) {:json-params trial}))

(defn root-component []
  [:div
   [app-bar]

   [:div.body-container
    [:> Typography
     {:variant "h4"
      :gutterBottom true}
     "Re-Trials Explorer"]

    [trials-listings @global-store edit-trial!]]])

(defn mount-tree
  "mount root node for reagent tree"
  []
  (r/render [root-component] (js/document.getElementById "app")))


;; REPL'ing ======================

;; Doing a POST with cljs-http
(comment (def answer (client/post "https://5dc26490.ngrok.io/trial/1" {:json-params {:name "No-AFTS-Here"}}))
         (async/go (let [res (async/<! answer)]
                     (println res))))
