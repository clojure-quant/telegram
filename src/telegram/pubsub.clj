(ns telegram.pubsub
  (:require
   [telegram.send :refer [send-message]]))

(defn has-chat? [chats chat-id]
  ;(println "has-chat? " chats " chat-id: " chat-id)
  (some #(= chat-id %) chats))

(defn subscriptions [{:keys [state] :as this}]
  (let [chat-id (:chat-id @state)
        subscriptions (:subscriptions @state)]
    (->> (filter (fn [[topic chats]]
                   (has-chat? chats chat-id))
                 subscriptions)
         (map first))))

(defn subscribe [{:keys [state] :as this} topic]
  (let [chat-id (:chat-id @state)
        subscriptions (:subscriptions @state)
        chats (or (get subscriptions topic) [])
        chats (if (has-chat? chats chat-id)
                chats
                (conj chats chat-id))]
    (swap! state update :subscriptions assoc topic chats)
    (println "topic " topic " subscribers: " chats " state: " @state)
    {:html (str "added subscription topic: " topic)}))

(defn unsubscribe [{:keys [state] :as this} topic]
  (let [chat-id (:chat-id @state)
        subscriptions (:subscriptions @state)
        chat-ids (->> (or (get subscriptions topic) [])
                      (remove #(= chat-id %)))]
    (swap! state update :subscriptions assoc topic chat-ids)
    (println "topic " topic " subscribers: " chat-ids " state: " @state)
    {:html (str "removed subscription topic: " topic)}))

(defn topic-subscribers [state topic]
  (let [subscriptions (:subscriptions @state)
        chat-ids (or (get subscriptions topic) [])]
    chat-ids))


(defn publish [{:keys [bot state]} topic msg]
  (let [chats (topic-subscribers state topic)]
    ;(println "publishing topic: " topic " to: " chats)
    (println "publishing topic: " topic " to: " (count chats) " subscribers .. " " chat-ids: " chats)
    (doall (map #(send-message bot % msg) chats))))


;; TOPIC: DEFAULT subscribe/unsubscribe

(defn get-chat-id [msg]
  (get-in msg [:chat :id]))

(defn subscribe-default [this]
  (let [topic "default"
        msg (subscribe this topic)]
    msg))

(defn unsubscribe-default [this]
  (let [topic "default"
        msg (unsubscribe this topic)]
    msg))

(defn my-subscriptions [this]
  (println "my-subs..")
  (let [topics (subscriptions this)
        msg {:html (str "Your subscriptions: " (pr-str topics))}]
    msg))