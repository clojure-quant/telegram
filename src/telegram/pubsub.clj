(ns telegram.pubsub
  (:require
   [taoensso.timbre :refer [info warn error]]
   [telegram.send :refer [send-message]]))

(defn has-chat? [chats chat-id]
  ;(info "has-chat? " chats " chat-id: " chat-id)
  (some #(= chat-id %) chats))

(defn add-current-session [{:keys [state] :as this} current-session]
  (swap! state assoc :current-session current-session)
  this)

(defn current-session [{:keys [state] :as this2}]
  (:current-session @state))

(defn current-session-chat-id [{:keys [state] :as this2}]
  (get-in @state [:current-session :chat-id]))

(defn subscriptions [{:keys [state] :as this}]
  (let [chat-id (current-session-chat-id this)
        subscriptions (:subscriptions @state)]
    (->> (filter (fn [[topic chats]]
                   (has-chat? chats chat-id))
                 subscriptions)
         (map first))))

(defn subscribe [{:keys [state] :as this} topic]
  (if-let [chat-id (current-session-chat-id this)]
    (let [subscriptions (:subscriptions @state)
          chats (or (get subscriptions topic) [])
          chats (if (has-chat? chats chat-id)
                  chats
                  (conj chats chat-id))]
      (swap! state update :subscriptions assoc topic chats)
      (info "topic " topic " subscribers: " chats " state: " @state)
      {:html (str "added subscription topic: " topic)})
    (let [session (current-session this)]
      {:html (str "subscription topic: " topic 
                  " cannot be added. unknown chat-id. session: " 
                  (pr-str session))})))

(defn unsubscribe [{:keys [state] :as this} topic]
  (let [chat-id  (current-session-chat-id this)
        subscriptions (:subscriptions @state)
        chat-ids (->> (or (get subscriptions topic) [])
                      (remove #(= chat-id %)))]
    (swap! state update :subscriptions assoc topic chat-ids)
    (info "topic " topic " subscribers: " chat-ids " state: " @state)
    {:html (str "removed subscription topic: " topic)}))

(defn topic-subscribers [{:keys [state]} topic]
  (let [subscriptions (:subscriptions @state)
        chat-ids (or (get subscriptions topic) [])]
    chat-ids))

(defn publish [{:keys [bot] :as this} topic msg]
  (let [chats (topic-subscribers this topic)]
    ;(info "publishing topic: " topic " to: " chats)
    (info "publishing topic: " topic " to: " (count chats) " subscribers .. " " chat-ids: " chats)
    (doall (map #(send-message bot % msg) chats))))

(defn my-subscriptions [this]
  (info "my-subs..")
  (let [topics (subscriptions this)
        msg {:html (str "Your subscriptions: " (pr-str topics))}]
    msg))