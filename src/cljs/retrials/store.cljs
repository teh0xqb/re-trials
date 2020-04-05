(ns retrials.store
  (:require
   [cljs-http.client :as client]
   ))

(defn hydrate
  "placeholder"
  []
  nil)

(defn edit-trial!
  "Received complete or partial info of a trial, and edits it on the server."
  [{:keys [id] :as trial}]
  (client/post (str "https://5dc26490.ngrok.io/trial/" id) {:json-params trial}))
