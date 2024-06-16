(ns telegram.command
  (:require
   [clojure.string :as str]
   [cemerick.url :refer (url url-encode)]
   [telegrambot-lib.core :as tbot]
   [telegram.send :refer [send-message]]))

; A command must always start with the '/' symbol and may not be longer than 32 characters. 
; Commands can use latin letters, numbers and underscores. Here are a few examples:
; /get_messages_stats
; /set_timer 10min Alarm!
; /get_timezone London, UK

(defn set-commands [b commands]
  (let [commands (map #(dissoc % :rpc-fn) commands)
        {:keys [ok result error_code description] :as r} (tbot/set-my-commands b {:commands commands})]
    (if ok
      (println "set-commands success! ")
      (println "set-commands error: " error_code ": " description))))

(defn msg-command? [{:keys [text entities] :as msg}]
  (let [entity-type (-> entities first :type)]
    ;(println "is-command? text: " text " type:" entity-type)
    (when (and (= "bot_command" entity-type)
               (str/starts-with? text "/"))
      (subs text 1  (count text)))))

(defn cmd= [user-command]
  (fn [{:keys [command]}]
    (str/starts-with? user-command command)))

(defn unknown-cmd [text]
  {:html (str "unknown command: " text)})



(defn process-command [bot commands state {:keys [text chat] :as msg} c]
  (println "processing command: " c)
  (swap! state assoc :command c)
  (let [{:keys [command rpc-fn]
         :or {rpc-fn unknown-cmd}}
        (or (->> (filter (cmd= c) commands) first)
            {:rpc-fn unknown-cmd})
        reply-msg (rpc-fn {:bot bot :commands commands :state state :msg msg :cmd c})
        {:keys [ok error_code description]} (send-message bot (:id chat) reply-msg)]
    (when-not ok
      (println "command [" command "] send error code: " error_code " description: " description " reply: \r\n" reply-msg))))

(defn process-text [bot commands state data {:keys [text] :as message}]
  (let [command (:command @state)]
  (println "message text rcvd: " text " command: " command)
  (println "message text data: " data " message: " message)))

(defn process-message [bot commands state {:keys [message] :as data}]
    (if-let [c (msg-command? message)]
      (process-command bot commands state message c)
      (process-text bot commands state data message)))
