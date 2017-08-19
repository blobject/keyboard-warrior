(ns keyboard-warrior-server.view
  (:require [hiccup.page :as h]))

(defn page [req]
  (h/html5
   [:head
    [:title "keyboard warrior"]
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1 maximum-scale=1, user-scalable=0"}]
    [:link {:href "https://fonts.googleapis.com/css?family=Source+Code+Pro"
            :rel "stylesheet"}]
    [:link {:href "https://fonts.googleapis.com/css?family=Droid+Serif"
            :rel "stylesheet"}]
    (h/include-css "/app/keyboard-warrior/css/style.css")]
   [:body
    [:div {:id "app"} "loading Keyboard Warrior ..."]
    [:script {:type "text/javascript"
              :src "/app/keyboard-warrior/js/compiled/kw.js"}]]))
