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
    :speed 0
    :duration 0
    :characters 0
    :words 0
    :typed []
    :hit []
    :last nil}))

(defn press [event]
  (let [alice corpus/alice
        state-t (:typed @state)
        state-h (:hit @state)
        count-t (count state-t)
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
          (let [new-t (assoc state-t count-t key)
                new-h (assoc state-h count-t (= key (nth alice count-t)))
                new-c (inc (:characters @state))]
            (swap! state assoc :typed new-t)
            (swap! state assoc :characters new-c)
            (swap! state assoc :hit new-h))
          back?
          (if (> count-t 0)
            (let [new-t (subvec state-t 0 (- count-t 1))
                  new-h (subvec state-h 0 (- count-t 1))]
              (swap! state assoc :typed new-t)
              (swap! state assoc :hit new-h))))
        (swap! state assoc :last key)))))

(defn tick []
  (let [playing? (= :playing (:status @state))
        new-d (inc (:duration @state))
        new-s (goog-string/format
               "%.1f"
               (-> (:characters @state) (/ new-d) (* 12)))]
    (if playing?
      (do
        (swap! state assoc :duration new-d)
        (swap! state assoc :speed new-s)))))

(defn stats-area []
  [:div {:class "stats"}
   [:div {:class "characters"}
    "characters: " (:characters @state)]
   [:div {:class "speed"}
    "speed: " (:speed @state) " wpm"]
   [:div {:class "duration"}
    "duration: " (:duration @state)]])

(defn model-area []
  [:div {:class "model"}
   [:code
    corpus/alice]])

(defn replica-area []
  [:code {:class "replica"}
   (apply str (:typed @state))
   [:br]
   (map #(if % "o" "x") (:hit @state))])

(defn last-area []
  [:div {:class "last"}
   (let [last (:last @state)]
     (condp = last
       " " "Space"
       last))])

(defn debug-area []
  [:div {:class "debug"}
   @state])

(defn kw []
  [:div {:class "frame"}
   [:h1 "keyboard warrior"]
   (stats-area)[:br]
   (model-area)[:br]
   "---"[:br]
   (replica-area)[:br][:br]
   (last-area)
   (debug-area)])

(js/setInterval tick 1000)

(.addEventListener js/document "keydown" press)

(reagent/render-component
 [kw]
 (. js/document (getElementById "app")))

(defn on-js-reload [])
