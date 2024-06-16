(ns telegram.service
  (:require
   [telegrambot-lib.core :as tbot]
   [telegram.command :as cmd]))

(defn next-update-id [result]
  ;(println "next-update-id for result: " result)
  (let [ids (map :update_id result)]
    (-> (apply max ids) inc)))

(defn create-get-update [bot]
  (let [id (atom nil)]
    (fn []
      ;; https://core.telegram.org/bots/api#getting-updates
      (println "get-updates max-id: " @id)
      (let [r (if @id
                (tbot/get-updates bot {:offset @id :timeout 5})
                (tbot/get-updates bot {:timeout 5}))
            ;_ (println "get-update result: " r)
            {:keys [ok result error_code description]} r]
        (when (and ok (not (empty? result)))
          (reset! id (next-update-id result)))
        #_(when ok
            (println "get-update success max id: " @id "msg rcvd: " result))
        (when-not ok
          (println "get-update error code: " error_code " error desc: " description))
        (if ok
          result
          [])))))

(defn start-polling [b state {:keys [commands msg-fn] :as opts}]
  (println "starting polling.......")
  (let [get-update (create-get-update b)
        process-msg (fn [data]
                      (cmd/process-message b commands state data))]
    (future
      (loop []
        (let [messages (get-update)]
          ;(println "messages received: " msg)
          (doall (map process-msg messages)))
        (Thread/sleep 500)
        (recur)))))

(defn telegram-bot-start [{:keys [token name _hook]} {:keys [commands] :as opts}]
  (println "telegram bot name: " name " starting ..")
  (let [bot (tbot/create token)
        state (atom {:command nil
                     :subscriptions {}})]
    (start-polling bot state opts)
    (cmd/set-commands bot commands)
    {:bot bot 
     :state state
     :commands commands}))


