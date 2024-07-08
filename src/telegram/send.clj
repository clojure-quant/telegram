(ns telegram.send
  (:require
   [taoensso.timbre :refer [info warn error]]
   [telegrambot-lib.core :as tbot]))

(defn send-message-raw [bot chat-id text opts]
  (let [{:keys [ok error_code description] :as reply} (tbot/send-message bot chat-id text opts)]
    (when-not ok
      (error "send-msg error code: " error_code " description: " description " opts: \r\n" opts))
    reply))

(defn send-message [bot chat-id {:keys [text html md keyboard web reply-keyboard photo]
                                 :or {text ""} :as msg}]
  (if msg
    (let [{:keys [text format]} (cond  html {:format "HTML" :text html}
                                       md {:format "MarkdownV2" :text md}
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
        (do (info "sending msg opts: " opts)
            (send-message-raw bot chat-id text opts))))
    (error "will not send empty message.")))

  