(ns retrials.lib
  (:require [reagent.core :as r]
            [cljs-http.client :as client]
            [cljs.core.async :as async]
            ))

(defn a
  "mount root node for reagent tree"
  (r/render [root-component] (js/))
  )

