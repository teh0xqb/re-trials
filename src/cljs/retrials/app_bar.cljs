(ns retrials.app-bar
  (:require
   [reagent.core :as r]
   ["@material-ui/core/AppBar" :default AppBar]
   ["@material-ui/core/Toolbar" :default ToolBar]
   ["@material-ui/core/Button" :default Button]
   ["@material-ui/icons/Menu" :default menu-icon]
   ;; ["@material-ui/core/colors/yellow" :default yellow-color]
   ["@material-ui/core/styles" :refer [withStyles]]))

(def DemoToolbar
  (->> ToolBar ((withStyles
                 (fn [theme]
                   (clj->js
                    {:root
                     {:background theme.palette.primary.main}}))))))

(defn app-bar []
  [:> AppBar {:style {:position "relative"}}
   [:> DemoToolbar {:disable-gutters true}
    [:> Button
     [:> menu-icon]]]])
