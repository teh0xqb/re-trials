(ns retrials.core
  (:require [retrials.lib :as lib]))

(defn main!
  "Main entry point- fetch initial data and mount react tree"
  []
  (lib/hydrate)
  (lib/mount-tree))

(defn reload!
  "For development purposes only"
  []
  (lib/mount-tree))

