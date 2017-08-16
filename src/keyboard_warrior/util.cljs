(ns keyboard-warrior.util
  (:require [goog.string :as goog-string]
            [goog.string.format]))

(defn round [x n]
  (goog-string/format (str "%." x "f") n))
