(ns keyboard-warrior-server.view
  (:require [hiccup.page :as h]))

(defn page [req]
  (h/html5
   [:head
    [:title "keyboard warrior"]
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1 maximum-scale=1, user-scalable=0"}]
    [:script "(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)})(window,document,'script','https://www.google-analytics.com/analytics.js','ga');ga('create', 'UA-104505539-2', 'auto');ga('send', 'pageview');"]
    [:link {:href "https://fonts.googleapis.com/css?family=Source+Code+Pro"
            :rel "stylesheet"}]
    [:link {:href "https://fonts.googleapis.com/css?family=Source+Serif+Pro&amp;subset=latin-ext"
            :rel "stylesheet"}]
    [:link {:href "https://fonts.googleapis.com/css?family=Droid+Serif"
            :rel "stylesheet"}]
    (h/include-css "/app/keyboard-warrior/css/style.css")]
   [:body
    [:div {:id "app"} "loading Keyboard Warrior ..."]
    [:script {:type "text/javascript"
              :src "/app/keyboard-warrior/js/compiled/kw.js"}]]))
