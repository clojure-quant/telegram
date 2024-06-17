(ns telegram.options
  (:require
   [telegram.send :refer [send-message]]
   [telegram.command :as cmd]
   [telegram.db :as db]))

(defn has-all-args? [opts args]
  (= (count opts) (count args)))

(defn exec-command [{:keys [bot state commands] :as this}]
  (let [c (:command @state)
        args (:args @state)
        {:keys [rpc-fn opts] :or {opts []}} (cmd/get-command this c)
        this2 (assoc this :cmd c)]
    (try
      (println "executing command: " c "with args: " args)
      (apply rpc-fn this2 args)
      (catch Exception ex
        (println "command " c " exception: " ex)
        {:html "an exception has occured in this command."}))))

(defn read-current-arg [{:keys [bot state commands] :as this} {:keys [message] :as data}]
  (println "++ reading arg ++")
  (let [{:keys [command args opts]} (db/get-command-state this)
        idx (count args)
        option (get opts idx)
        {:keys [title]} option
        {:keys [text chat]} message]
    (println "received arg: " command "#" title "  value: " text)
    (db/add-arg this text)
    (println "new args state: " (:args @state))))

(defn get-next-arg-message [{:keys [bot state commands] :as this}]
  (let [{:keys [command args opts]} (db/get-command-state this)
        idx (count args)
        option (get opts idx)
        {:keys [title]} option]
    (println "get-next-arg cmd: " command " args: " args " current option: " option)
    {:html (str "please enter argument: " title)}))


(defn create-reply-message [{:keys [bot state commands] :as this} {:keys [message] :as data}]
  (let [c1 (cmd/msg-command? message)]
    (when c1  (db/set-command! this c1))
    (let [{:keys [command args opts]} (db/get-command-state this)]
      (println "++ reply cmd: " command "args: " args)
      (when (and (not c1) (not (has-all-args? opts args)))
        (read-current-arg this data))
      (let [{:keys [args opts]} (db/get-command-state this)]
        (if (has-all-args? opts args)
          (exec-command this)
          (get-next-arg-message this))))))

(defn process-message [{:keys [bot state commands] :as this} {:keys [message] :as data}]
  (println "message text data: " data " message: " message)
  (let [{:keys [text chat]} message]
    (->> (create-reply-message this data)
         (send-message bot (:id chat)))))



