(ns keyboard-warrior.input
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [keyboard-warrior.figure :as figure]
            [keyboard-warrior.state :refer [state]]
            [cljs.core.async :refer [<! timeout]]
            [taoensso.sente :as s]))

;; sente

(let [get-path (fn [] (-> js/document .-location .-pathname))
      {:keys [chsk ch-recv send-fn state]}
      (s/make-channel-socket-client!
       (str (get-path) "sock") {:type :auto})]
  (def sock chsk)
  (def ch-sock ch-recv)
  (def sock-send! send-fn)
  (def sock-state state))

;; sente event handling

(defn ws-press [{:keys [speed characters]}]
  (sock-send!
   [:kw/press
    {:speed speed
     :characters characters}]))

(defn ws-world [msg]
  (let [{:keys [client speed characters]} (last msg)
        cid (first (clojure.string/split client "-"))
        state-w (:world @state)
        new-w (assoc state-w (keyword cid)
                     {:speed speed :characters characters})]
    (swap! state assoc :world new-w)))

(defmulti -sock-handler :id)

(defn sock-handler
  [{:as msg :keys [id ?data event]}]
  (-sock-handler msg))

(defmethod -sock-handler :default
  [{:as msg :keys [event]}]
  #_(prn (str "unhandled event: " event)))

(defmethod -sock-handler :chsk/state
  [{:as msg :keys [?data]}]
  #_(let [[old-state-map new-state-map] ?data]
    (if (:first-open? new-state-map)
      (prn (str "socket established: " new-state-map)))))

(defmethod -sock-handler :chsk/recv
  [{:as msg :keys [?data]}]
  #_(prn (str "from server: " ?data))
  (ws-world ?data))

(defmethod -sock-handler :chsk/handshake
  [{:as msg :keys [?data]}]
  #_(let [[?uid ?csrf-token ?handshake-data] ?data]
    (prn (str "handshake: " ?data))))

(defonce sock_ (atom nil))

(defn stop-sock! []
  (when-let [stop @sock_]
    (stop)
    (reset! sock_ nil)))

(defn start-sock! []
  (stop-sock!)
  (reset! sock_ (s/start-client-chsk-router! ch-sock sock-handler)))

;; game event handling

(defn press [key]
  (let [alice figure/alice
        state-t (:typed @state)
        state-h (:hit @state)
        hits (count state-h)
        new-gc (inc (:gross-characters @state))
        playing? (= :playing (:status @state))
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
        (swap! state assoc :last key)
        (ws-press @state)))))

(defn clear! []
  (go-loop []
    (swap! state assoc :world {})
    (<! (timeout (* 1000 300)))
    (recur)))

;; init

(defonce start-once
  (do
    (start-sock!)
    (clear!)))

(.addEventListener
 js/document "keydown"
 (fn [event] (press (.-key event))))
