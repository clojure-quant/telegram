(ns telegram.command
  (:require
   [clojure.string :as str]
   [telegrambot-lib.core :as tbot]))

; A command must always start with the '/' symbol and may not be longer than 32 characters. 
; Commands can use latin letters, numbers and underscores. Here are a few examples:
; /get_messages_stats
; /set_timer 10min Alarm!
; /get_timezone London, UK

(defn cmd= [user-command]
  (fn [{:keys [command]}]
    (str/starts-with? user-command command)))

(defn get-command [{:keys [bot state commands] :as this} cmd]
  (->> (filter (cmd= cmd) commands) first))

(defn set-commands [b commands]
  (let [commands (map #(select-keys % [:command :description]) commands)
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


(defn unknown-cmd [text]
  {:html (str "unknown command: " text)})

