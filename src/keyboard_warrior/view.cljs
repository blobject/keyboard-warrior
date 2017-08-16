(ns keyboard-warrior.view
  (:require [keyboard-warrior.figure :as figure]
            [keyboard-warrior.state :refer [state]]
            [keyboard-warrior.util :as util]
            [goog.string :as goog-string]
            [goog.string.format]
            [reagent.core :as reagent]))

(defn stats []
  (let [{:keys [gross-characters
                characters
                wrong
                gross-speed
                speed
                duration]} @state
        wrong-rate (->> (/ wrong (if (= gross-characters 0) 1 gross-characters))
                        (* 100)
                        (util/round 2))]
    [:div {:class "stats"}
     [:div {:class "gross-characters"}
      "gross characters: " gross-characters]
     [:div {:class "characters"}
      "characters: " characters]
     [:div {:class "wrong"}
      "wrong: " wrong " (" wrong-rate "%)"]
     [:div {:class "gross-speed"}
      "gross speed: " gross-speed ]
     [:div {:class "speed"}
      "speed: " speed " wpm"]
     [:div {:class "duration"}
      "duration: " duration]]))

(defn figure []
  (let [figure figure/alice
        state-h (:hit @state)
        count-h (count state-h)
        groups (partition-by #(true? (last %)) state-h)]
    [:div {:class "figure"}
     (map-indexed
      (fn [i group]
        (let [rights? (true? (last (first group)))
              class (if rights? "good" "bad")
              color (if rights? "green" "red")
              bg-color (if rights? "#f0f0f0" "#ffd8d8")]
          [:code {:key i
                  :class (str "typed " class)
                  :style {:font-weight 600
                          :color color
                          :background-color bg-color}}
           (subs figure (ffirst group) (+ (first (last group)) 1))]))
      groups)
     [:code {:class "rest"} (subs figure count-h (count figure))]]))

(defn replica []
  [:code {:class "replica"}
   (apply str (:typed @state))])

(defn last-key []
  [:div {:class "last"}
   "> "
   (let [state-l (:last @state)]
     (condp = state-l
       " " "Space"
       state-l))])

(defn debug []
  [:div {:class "debug" :style {:color "#808080"}}
   @state])
