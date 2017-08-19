(ns keyboard-warrior.util
  (:require [goog.string :as goog-string]
            [goog.string.format]))

(def home-path "https://alocy.be")
(def page-path "https://alocy.be/page/keyboard-warrior")
(def git-path "https://github.com/agarick/keyboard-warrior")

(defn round [x n]
  (goog-string/format (str "%." x "f") n))
