(ns keyboard-warrior.core
  (:require [keyboard-warrior.input :as input]
            [keyboard-warrior.state :refer [state]]
            [keyboard-warrior.util :as util]
            [keyboard-warrior.view :as view]
            [reagent.core :as reagent]))

(enable-console-print!)

(defn tick []
  (let [playing? (= :playing (:status @state))
        new-d (inc (:duration @state))
        wrongs (count (filter #(false? (last %)) (:hit @state)))
        new-s (util/round
               1 (-> (:characters @state) (- wrongs) (/ new-d) (* 12)))
        new-gs (util/round
                1 (-> (:gross-characters @state) (/ new-d) (* 12)))]
    (if playing?
      (do
        (swap! state assoc
               :duration new-d
               :speed new-s
               :gross-speed new-gs)))))

(defn keyboard-warrior []
  (view/all @state))

(js/setInterval tick 1000)

(reagent/render-component
 [keyboard-warrior]
 (. js/document (getElementById "app")))

(defn on-js-reload [])
