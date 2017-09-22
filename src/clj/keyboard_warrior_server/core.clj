(ns keyboard-warrior-server.core
  (:require [keyboard-warrior-server.view :as view]
            [compojure.core :as cc]
            [compojure.handler :as ch]
            [compojure.route :as cr]
            [hiccup.page :as hp]
            [hiccup.util :as hu]
            [org.httpkit.server :as http]
            [ring.middleware.defaults :as rd]
            [ring.middleware.reload :as rr]
            [taoensso.sente :as s]
            [taoensso.sente.server-adapters.http-kit :refer (sente-web-server-adapter)])
  (:gen-class))

;; sente

(let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn connected-uids]}
      (s/make-channel-socket! sente-web-server-adapter {})]
  (def ring-ajax-post ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake
    ajax-get-or-ws-handshake-fn)
  (def ch-sock        ch-recv)
  (def sock-send!     send-fn)
  (def connected-uids connected-uids))

;; ring

(cc/defroutes ring-routes
  (cc/GET  "/"     req (view/page req))
  (cc/GET  "/sock" req (ring-ajax-get-or-ws-handshake req))
  (cc/POST "/sock" req (ring-ajax-post req))
  (cr/resources "/dev/keyboard-warrior")
  (cr/not-found "not found"))

(def ring-handler
  (rr/wrap-reload (rd/wrap-defaults #'ring-routes rd/site-defaults)))

;; sente event handling

(defmulti -sock-handler :id)

(defn sock-handler
  [{:as msg :keys [id ?data event]}]
  (-sock-handler msg))

(defmethod -sock-handler :default
  [{:as msg :keys [event id ?data req ?reply-fn send-fn]}]
  (let [session (:session req)
        uid (:uid session)]
    #_(prn (str "unhandled event: " event))
    (when ?reply-fn
      (?reply-fn {:unmatched-from-server event}))))

(defmethod -sock-handler :kw/press
  [{:as msg :keys [ring-req id event]}]
  (let [uids (:any @connected-uids)
        {:keys [speed characters]} (last event)]
    (doseq [uid uids]
      (sock-send!
       uid
       [:kw/world
        {:client (-> ring-req :params :client-id)
         :speed speed
         :characters characters}]))))

(defonce sock_ (atom nil))

(defn stop-sock! []
  (when-let [stop @sock_]
    (stop)
    (reset! sock_ nil)))

(defn start-sock! []
  (stop-sock!)
  (reset! sock_ (s/start-server-chsk-router! ch-sock sock-handler)))

;; server handling

(defonce server_ (atom nil))

(defn stop-server! []
  (when-let [stop @server_]
    (stop)
    (reset! server_ nil)))

(defn start-server! [port]
  (stop-server!)
  (let [port (Integer. (or port 8010))
        stop (http/run-server ring-handler {:port port})]
    (reset! server_ stop)))

;; init

(defn start! [port]
  (start-sock!)
  (start-server! port))

(defn -main [& [port]]
  (start! port))
