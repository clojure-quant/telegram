(ns telegram.session
  (:require
   [telegram.command :as cmd]
   [telegram.message :as msg]))

(defn set-command! [{:keys [state] :as this} command data]
  (let [{:keys [rpc-fn opts] :or {opts []}} (cmd/get-command this command)
        chat-id (msg/chat-id data)]
    (println "setting session command: " command " chat-id: " chat-id)
    (swap! state assoc-in [:session chat-id] 
           {:command command
            :args []
            :opts opts
            :rpc-fn rpc-fn
            :chat-id chat-id}
           )))

(defn get-command-state [{:keys [state] :as this} chat-id]
  (get-in @state [:session chat-id]))

(defn add-arg [{:keys [state] :as this} chat-id msg-text]
  (let [{:keys [args]} (get-command-state this chat-id)
        args-new (conj args msg-text)]
    (swap! state assoc-in [:session chat-id :args]  args-new)))



  
