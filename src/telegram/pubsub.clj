(ns telegram.pubsub
  (:require
   [telegram.send :refer [send-message]]))

(defn subscribe [state topic chat-id]
  (let [subscriptions (:subscriptions @state)
        chat-ids (-> (or (get subscriptions topic) [])
                     (conj chat-id))]
    (swap! state update :subscriptions assoc topic chat-ids)
    (println "topic " topic " subscribers: " chat-ids " state: " @state)
    {:html (str "added subscription topic: " topic)}
    ))

(defn unsubscribe [state topic chat-id]
  (let [subscriptions (:subscriptions @state)
        chat-ids (->> (or (get subscriptions topic) [])
                      (remove #(= chat-id %)))]
    (swap! state update :subscriptions assoc topic chat-ids)
    (println "topic " topic " subscribers: " chat-ids " state: " @state)
    {:html (str "removed subscription topic: " topic)}))

(defn topic-subscribers [state topic]
  (let [subscriptions (:subscriptions @state)
        chat-ids (or (get subscriptions topic) [])]
    chat-ids))


(defn publish [bot state topic msg]
  (let [chat-ids (topic-subscribers state topic)]
    (println "publishing topic: " topic " to: " (count chat-ids) " subscribers .. " " chat-ids: " chat-ids)
    (doall (map #(send-message bot % msg) chat-ids))))
  

;; TOPIC: DEFAULT subscribe/unsubscribe

(defn get-chat-id [msg]
  (get-in msg [:chat :id]))

(defn subscribe-default [{:keys [bot state msg]}]
  (let [topic "default"
        chat-id (get-chat-id msg)
        msg (subscribe state topic chat-id)]
    msg))

(defn unsubscribe-default [{:keys [bot state msg]}]
  (let [topic "default"
        chat-id (get-chat-id msg)
        msg (unsubscribe state topic chat-id)]
    msg))