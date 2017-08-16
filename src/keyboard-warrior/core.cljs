(ns kw.core
  (:require [kw.corpus :as corpus]
            [goog.string :as goog-string]
            [goog.string.format]
            [reagent.core :as reagent]))

(enable-console-print!)

(defonce state
  (reagent/atom
   {:text "foo"
    :status :not-playing
    :gross-characters 0
    :characters 0
    :gross-speed 0
    :speed 0
    :duration 0
    :words 0
    :typed []
    :hit []
    :last nil}))

(defn press [event]
  (let [alice corpus/alice
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
                new-h (assoc state-h count-h [count-h (= key (nth alice count-h))])
                new-c (inc (:characters @state))]
            (swap! state assoc :typed new-t)
            (swap! state assoc :characters new-c)
            (swap! state assoc :gross-characters new-gc)
            (swap! state assoc :hit new-h))
          back?
          (if (> count-h 0)
            (let [new-t (subvec state-t 0 (- count-h 1))
                  new-h (subvec state-h 0 (- count-h 1))]
              (swap! state assoc :typed new-t)
              (swap! state assoc :hit new-h)
              (swap! state assoc :gross-characters new-gc))))
        (swap! state assoc :last key)))))

(defn tick []
  (let [playing? (= :playing (:status @state))
        new-d (inc (:duration @state))
        wrongs (count (filter #(false? (last %)) (:hit @state)))
        partial-round (partial goog-string/format "%.1f")
        new-s (partial-round
               (-> (:characters @state) (- wrongs) (/ new-d) (* 12)))
        new-gs (partial-round
                (-> (:gross-characters @state) (/ new-d) (* 12)))]
    (if playing?
      (do
        (prn wrongs)
        (swap! state assoc :duration new-d)
        (swap! state assoc :speed new-s)
        (swap! state assoc :gross-speed new-gs)))))

(defn stats-area []
  [:div {:class "stats"}
   [:div {:class "gross-characters"}
    "gross characters: " (:gross-characters @state)]
   [:div {:class "characters"}
    "characters: " (:characters @state)]
   [:div {:class "gross-speed"}
    "gross speed: " (:gross-speed @state)]
   [:div {:class "speed"}
    "speed: " (:speed @state) " wpm"]
   [:div {:class "duration"}
    "duration: " (:duration @state)]])

(defn model-area []
  (let [corpus corpus/alice
        state-h (:hit @state)
        count-h (count state-h)
        groups (partition-by #(true? (last %)) state-h)]
    [:div {:class "model"}
     (for [group groups
           :let [rights? (true? (last (first group)))
                 class (if rights? "good" "bad")
                 color (if rights? "green" "red")
                 bg-color (if rights? "#f0f0f0" "#ffd8d8")]]
       (map-indexed
        (fn [i hit]
          [:code {:key i
                  :class (str "typed " class)
                  :style {:font-weight 600
                          :color color
                          :background-color bg-color}}
           (nth corpus (first hit))])
        group))
     [:code {:class "rest"} (subs corpus count-h (count corpus))]]))

(defn replica-area []
  [:code {:class "replica"}
   (apply str (:typed @state))])

(defn last-area []
  [:div {:class "last"}
   "> "
   (let [last (:last @state)]
     (condp = last
       " " "Space"
       last))])

(defn debug-area []
  [:div {:class "debug" :style {:color "#808080"}}
   @state])

(defn kw []
  [:div {:class "frame"}
   [:h1 "keyboard warrior"]
   (stats-area)[:br]
   (model-area)[:br]
   "---"[:br]
   (replica-area)[:br][:br]
   (last-area)[:br]
   (debug-area)])

(js/setInterval tick 1000)

(.addEventListener js/document "keydown" press)

(reagent/render-component
 [kw]
 (. js/document (getElementById "app")))

(defn on-js-reload [])
