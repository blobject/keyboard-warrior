(ns keyboard-warrior.util
  (:require [goog.string :as goog-string]
            [goog.string.format]))

(def home-path "https://b.agaric.net")
(def page-path "https://b.agaric.net/dev")
(def git-path "https://github.com/blobject/keyboard-warrior")

(defn round [x n]
  (goog-string/format (str "%." x "f") n))
