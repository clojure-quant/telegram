(ns telegram.command
  (:require
   [taoensso.timbre :refer [info warn error]]
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


(defn valid-command-name? [c]
  (and (string? c)
       (<= (count c)  32)))

(defn valid-command? [s {:keys [command]}]
  (and s (valid-command-name? command)))

(defn valid-commands? [commands]
  (reduce valid-command? true commands))

(defn set-commands [b commands]
  (if (valid-commands? commands)
  (let [commands (map #(select-keys % [:command :description]) commands)
        {:keys [ok result error_code description] :as r} (tbot/set-my-commands b {:commands commands})]
    (if ok
      (info "set-commands success! ")
      (error "set-commands error: " error_code ": " description)))
    (do (error "invalid command found. commands need to be alphanumeric with max size 32.")
        (throw (Exception. "invalid telegram command found. max size: 32")))))

(defn msg-command? [{:keys [text entities] :as msg}]
  (let [entity-type (-> entities first :type)]
    ;(info "is-command? text: " text " type:" entity-type)
    (when (and (= "bot_command" entity-type)
               (str/starts-with? text "/"))
      (subs text 1  (count text)))))

(comment
  (valid-command-name? "1234")
  (valid-command-name? "01234567890123456789012345678901")
  (valid-command-name? "012345678901234567890123456789012")
 ; 
  )


