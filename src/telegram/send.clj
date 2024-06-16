(ns telegram.send
  (:require
   [clojure.string :as str]
   [telegrambot-lib.core :as tbot]))

(defn sanitize-markdown [md]
  (println "raw md: " md)
  (let [md (str/replace md #"|" "\\|")
        ;md (url-encode md)
        ]
    (println "sanitized md: " md)
    md))

(defn send-message-raw [bot chat-id text opts]
  (let [{:keys [ok error_code description] :as reply} (tbot/send-message bot chat-id text opts)]
    (when-not ok
      (println "send-msg error code: " error_code " description: " description " opts: \r\n" opts))
    reply))


(defn send-message [bot chat-id {:keys [text html md keyboard web reply-keyboard photo]
                                 :or {text ""} :as msg}]
  (if msg
    (let [{:keys [text format]} (cond  html {:format "HTML" :text html}
                                       md {:format "MarkdownV2" :text md #_(sanitize-markdown md)}
                                       :else {:format nil :text text})
          opts (if format
                 {:parse_mode format}
                 {})
          opts (if keyboard
                 (assoc opts :reply_markup {:inline_keyboard keyboard})
                 opts)
          opts (if reply-keyboard
                 (assoc opts :reply_markup  {:keyboard reply-keyboard
                                             :one_time_keyboard true})
                 opts)

          opts (if web
                 (assoc opts :web_app web)
                 opts)]
      (if photo
        (tbot/send-photo bot chat-id photo)
        (do (println "sending msg opts: " opts)
            (send-message-raw bot chat-id text opts))))
    (println "will not send empty message.")))

  