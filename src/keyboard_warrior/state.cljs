(ns keyboard-warrior.state
  (:require [reagent.core :as reagent]))

(defonce state
  (reagent/atom
   {:status :not-playing
    :gross-characters 0
    :characters 0
    :wrong 0
    :gross-speed 0
    :speed 0
    :duration 0
    :words 0
    :typed []
    :hit []
    :last "Start typing ..."
    :last-type :not-printable}))
