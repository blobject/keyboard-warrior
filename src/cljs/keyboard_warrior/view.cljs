(ns keyboard-warrior.view
  (:require [keyboard-warrior.figure :as figure]
            [keyboard-warrior.state :refer [state]]
            [keyboard-warrior.util :as util]
            [goog.string :as goog-string]
            [goog.string.format]
            [reagent.core :as reagent]))

(defn prompt [state]
  (let [state-l (:last state)
        state-lt (:last-type state)]
    [:div {:class (str "prompt" (if (= state-lt :printable) " printable"))}
     (condp = state-l
       " " "Space"
       state-l)]))

(defn speed [state]
  (let [{:keys [speed gross-speed hit wrong]} state
        hits (count hit)
        accuracy (->> (/ wrong
                         (if (= hits 0) 1 hits))
                      (* 100)
                      (- 100)
                      (util/round 2))]
    [:div {:class "speed"}
     speed
     " wpm "
     [:span {:class "gross"} gross-speed]
     " "
     [:span {:class "accuracy"} (if (= hits 0) 0 accuracy) "%"]]))

(defn characters [state]
  (let [{:keys [gross-characters characters wrong]} state]
    [:div {:class "characters"}
     characters
     " char "
     [:span {:class "gross"} gross-characters]
     " "
     [:span {:class "wrong"} wrong]]))

(defn format-duration [secs]
  (let [pad (partial goog-string/format "%02d")]
    (str (if (>= secs 3600) (-> secs (quot 3600) pad (str ":")))
         (if (>= secs 60) (-> secs (rem 3600) (quot 60) pad (str ":")))
         (pad (rem secs 60)))))

(defn duration [state]
  (let [duration (:duration state)]
  [:div {:class "duration"}
   (format-duration duration)]))

(defn figure [state]
  (let [figure figure/alice
        state-h (:hit state)
        hits (count state-h)
        groups (partition-by #(true? (last %)) state-h)]
    [:div {:class "figure"}
     (map-indexed
      (fn [i group]
        (let [[first-key first-val] (first group)
              [last-key _] (last group)
              rights? (true? first-val)
              class (if rights? "good" "bad")]
          [:span {:key i
                  :class (str "typed " class)}
           (subs figure first-key (+ last-key 1))]))
      groups)
     [:span {:class "rest"} (subs figure hits (count figure))]]))

(defn replica [state]
  [:code {:class "replica"}
   (apply str (:typed state))])

(defn help [])

(defn world [state]
  (let [list (map #(hash-map
                    :user (name (first %))
                    :speed (:speed (last %))
                    :characters (:characters (last %)))
                  (:world state))
        users (reverse (sort-by :speed list))]
    [:ol {:class "world"}
     (map-indexed
      (fn [i user]
        [:li {:key i}
         [:span {:class "user-name"}
          (:user user) ": "]
         [:span {:class "user-speed"}
          (:speed user)]
         [:span {:class "user-characters"}
          " (" (:characters user) ")"]])
      users)]))

(defn link []
  [:ul {:class "link"}
   [:li [:a {:href util/page-path} "about Keyboard Warrior"]]
   [:li [:a {:href util/git-path} "view source"]]])

(defn top [state]
  [:div {:class "top"}
   [:div {:class "left"}
    (prompt state)]
   [:div {:class "center"}
    (speed state)
    (characters state)]
   [:div {:class "right"}
    (duration state)]])

(defn middle [state]
  [:div {:class "middle"}
   (figure state)])

(defn bottom [state]
  [:div {:class "bottom"}
   (help)
   (world state)
   (link)])

(defn all [state]
  [:div {:id "app"}
   [:div {:class "frame"}
    (top state)
    (middle state)
    (bottom state)
    #_(view/debug)
    ]])

(defn debug [state]
  [:div {:class "debug" :style {:color "#808080"}}
   state])
