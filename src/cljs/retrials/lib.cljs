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


(defonce global-store (r/atom []))

;; TODO mock the store
;; TODO define config for urls and constants

(defn fetch-trials! []
  (async/go
    (let [response (async/<! (client/get "https://5dc26490.ngrok.io/trials"))]
      (when (:success response)
        (reset! global-store (:body response))))))

;; dev only
(add-watch global-store :global-store-watcher (fn [key atom old-state new-state]
                                     (pprint new-state)))

(defn edit-trial!
  "Receives complete or partial info of a trial, and edits it on the server.
  Returns a channel that communicates response async."
  [{:keys [id] :as trial}]
  (let [caller-chan (async/chan)
        http-chan (client/post (str "https://5dc26490.ngrok.io/trial/" id) {:json-params trial})]
    (async/go
      (let [response (async/<! http-chan)]
        (when (not (nil? response))
          (async/>! caller-chan
                    (if (:success response)
                      (do
                        (fetch-trials!)
                        {:success true})
                      {:success false
                       :msg (if (empty? (:body response))
                              "Failed."
                              (:body response))})))))
    caller-chan))

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
