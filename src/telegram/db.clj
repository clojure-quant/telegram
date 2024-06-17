(ns telegram.db
  (:require
   [telegram.command :as cmd]
   [telegram.message :as msg]))

(defn set-command! [{:keys [state] :as this} command data]
  (let [{:keys [rpc-fn opts] :or {opts []}} (cmd/get-command this command)
        chat-id (msg/chat-id data)]
    (println "setting command: " command " chat-id: " chat-id)
    (swap! state assoc
           :command command
           :args []
           :opts opts
           :rpc-fn rpc-fn
           :chat-id chat-id)))

(defn get-command-state [{:keys [bot state commands] :as this}]
  (-> @state (select-keys [:command :args :opts :rpc-fn])))

(defn add-arg [{:keys [bot state commands] :as this} msg-text]
  (let [{:keys [command args opts]} (get-command-state this)
        args-new (conj args msg-text)]
    (swap! state assoc :args args-new)))



  
