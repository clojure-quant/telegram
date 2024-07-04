(ns telegram.options
  (:require
   [taoensso.timbre :refer [info warn error]]
   [telegram.send :refer [send-message]]
   [telegram.command :as cmd]
   [telegram.session :as db]
   [telegram.dialog :as dialog]
   [telegram.message :as msg]
   [telegram.pubsub :as pubsub]))

(defn has-all-args? [opts args]
  (= (count opts) (count args)))

(defn exec-command [this chat-id]
  (let [{:keys [rpc-fn args command] :as current-session} (db/get-command-state this chat-id)
        this2 (pubsub/add-current-session this current-session)]
    (try
      (info "executing command: " command "with args: " args)
      (apply rpc-fn this2 args)
      (catch Exception ex
        (error "command " command " exception: " ex)
        {:html "an exception has occured in this command."}))))

(defn read-current-arg [{:keys [bot state commands] :as this} data]
  (info "++ reading arg ++")
  (let [chat-id (msg/chat-id data)
        {:keys [command args opts]} (db/get-command-state this chat-id)
        idx (count args)
        option (get opts idx)
        {:keys [title]} option
        text (msg/msg-text data)]
    (info "received arg: " command "#" title "  value: " text)
    (db/add-arg this chat-id text)
    (info "new args state: " (:args @state))))

(defn create-dialog [title options]
  (info "create-dialog title: " title " options: " options)
  (if options
    {:text (str "please select: " title)
     :keyboard (dialog/keyboard-options options)}
    {:html (str "please enter: " title)}))

(defn get-next-arg-message [{:keys [bot state commands] :as this} chat-id]
  (let [{:keys [command args opts]} (db/get-command-state this chat-id)
        idx (count args)
        option (get opts idx)
        {:keys [title options]} option]
    (info "get-next-arg cmd: " command " args: " args " current option: " option)
    (create-dialog title options)))


(defn unknown-reply [this data]
  (let [text (msg/msg-text data)]
    {:html (str "unknown command: " text)}))

(defn create-reply-message [{:keys [bot state commands] :as this} {:keys [message] :as data}]
  (let [c1 (cmd/msg-command? message)]
    (when c1  (db/set-command! this c1 data))
    (let [chat-id (msg/chat-id data)
          {:keys [command args opts]} (db/get-command-state this chat-id)]
      (info "++ reply cmd: " command "args: " args "command: " command)
      (if command
        (do (when (and (not c1) (not (has-all-args? opts args)))
              (read-current-arg this data))
            (let [{:keys [args opts]} (db/get-command-state this chat-id)]
              (if (has-all-args? opts args)
                (exec-command this chat-id)
                (get-next-arg-message this chat-id))))
        (unknown-reply this data)))))

(defn process-message [{:keys [bot state commands] :as this} data]
  (info "RCVD: " data)
  (let [chat-id (msg/chat-id data)]
    (->> (create-reply-message this data)
         (send-message bot chat-id))))



