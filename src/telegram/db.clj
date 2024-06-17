(ns telegram.db
   (:require
   [telegram.command :as cmd]))



(defn set-command! [{:keys [bot state commands] :as this} command]
  (let [{:keys [rpc-fn opts] :or {opts []}} (cmd/get-command this command)]
    (println "setting command: " command)
    (swap! state assoc 
           :command command
           :args []
           :opts opts
           :rpc-fn rpc-fn
           )))

(defn get-command-state [{:keys [bot state commands] :as this}]
  (-> @state (select-keys [:command :args :opts :rpc-fn])))

(defn add-arg [{:keys [bot state commands] :as this} msg-text]
  (let [{:keys [command args opts]} (get-command-state this)
         args-new (conj args msg-text)]
      (swap! state assoc :args args-new)))



  
