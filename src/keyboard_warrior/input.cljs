(ns keyboard-warrior.input
  (:require [keyboard-warrior.figure :as figure]
            [keyboard-warrior.state :refer [state]]))

(defn press [event]
  (let [alice figure/alice
        state-t (:typed @state)
        state-h (:hit @state)
        hits (count state-h)
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
          (let [new-t (assoc state-t hits key)
                new-h (assoc state-h hits
                             [hits (= key (nth alice hits))])
                new-c (inc (:characters @state))
                new-w (count (filter #(false? (last %)) new-h))]
            (swap! state assoc
                   :last-type :printable
                   :typed new-t
                   :characters new-c
                   :gross-characters new-gc
                   :hit new-h
                   :wrong new-w))
          back?
          (if (> hits 0)
            (let [new-t (subvec state-t 0 (- hits 1))
                  new-h (subvec state-h 0 (- hits 1))
                  new-w (count (filter #(false? (last %)) new-h))]
              (swap! state assoc
                     :last-type :not-printable
                     :typed new-t
                     :hit new-h
                     :gross-characters new-gc
                     :wrong new-w)))
          (not printable?)
          (swap! state assoc :last-type :not-printable))
        (swap! state assoc :last key)))))
