(ns retrials.lib
  (:require [reagent.core :as r]
            [cljs-http.client :as client]
            [cljs.core.async :as async]
            ))

(defn root-component []
  [:div
   [:p "Hello World"]])

(defn mount-tree
  "mount root node for reagent tree"
  []
  (r/render [root-component] (js/document.getElementById "app")))

