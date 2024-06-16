(ns telegram.hook
  (:require
   [modular.webserver.middleware.api :refer [wrap-api-handler]]))

(defn telegram-hook-handler [{:keys [body-params query-params params] :as req}]
  (println "telegram-hook-event: " (keys req)))

(def telegram-hook-handler-wrapped
  (wrap-api-handler telegram-hook-handler))



    #_(when hook
    (println "setting telegram hook-url to: " hook)
    (let [r (tbot/set-webhook bot hook)]
      (println "set-telegram-hook-result: " r))
    (let [r (tbot/get-webhook-info bot)]
      (println "get-telegram-hook-info: " r)))


      ; a different api: https://github.com/Otann/morse

; ssl certs for morse:
; http://blog.hellonico.info/clojure/telegram_bot/