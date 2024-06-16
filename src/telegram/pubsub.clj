(ns telegram.pubsub
  (:require
   [clojure.set :as set]))

(defn get-chat-id [msg]
  (get-in msg [:chat :id]))

(defn start []
  (atom #{}))

(defn subscribe [state msg]
  (let [chat-id (get-chat-id msg)]
    (swap! state set/union #{chat-id})
    (println "subscribers: " @state)))

(defn unsubscribe [state msg]
  (let [chat-id (get-chat-id msg)]
    (swap! state disj chat-id)
    (println "subscribers: " @state)))

(defn publish-to [bot chat-id msg])

(defn publish [state data]
  (let [chat])
  
  )
  