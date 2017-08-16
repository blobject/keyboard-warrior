(ns keyboard-warrior.input
  (:require [keyboard-warrior.figure :as figure]
            [keyboard-warrior.state :refer [state]]))

(defn press [event]
  (let [alice figure/alice
        state-t (:typed @state)
        state-h (:hit @state)
        count-h (count state-h)
        new-gc (inc (:gross-characters @state))
        playing? (= :playing (:status @state))
        key (.-key event)
        back? (= key "Backspace")
        printable-regex #"[ \w`~!@#$%^&*\(\)\-_=+\[{\]}\\\|;:'\",<.>/?]"
        printable? (re-matches printable-regex key)]
    (if
      (some? (or printable? back?))
      (do
        (if-not playing?
          (swap! state assoc :status :playing))
        (cond
          printable?
          (let [new-t (assoc state-t count-h key)
                new-h (assoc state-h count-h
                             [count-h (= key (nth alice count-h))])
                new-c (inc (:characters @state))
                new-w (count (filter #(false? (last %)) new-h))]
            (swap! state assoc
                   :typed new-t
                   :characters new-c
                   :gross-characters new-gc
                   :hit new-h
                   :wrong new-w))
          back?
          (if (> count-h 0)
            (let [new-t (subvec state-t 0 (- count-h 1))
                  new-h (subvec state-h 0 (- count-h 1))
                  new-w (count (filter #(false? (last %)) new-h))]
              (swap! state assoc
                     :typed new-t
                     :hit new-h
                     :gross-characters new-gc
                     :wrong new-w))))
        (swap! state assoc :last key)))))

(.addEventListener js/document "keydown" press)
