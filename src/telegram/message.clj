(ns telegram.message)


(defn chat-id-raw [data]
  (or (get-in data [:message :chat :id])
      (get-in data [:callback_query :message :chat :id])))
      

(defn chat-id [data]
  (let [id (chat-id-raw data)]
    (println "chat-id: " id)
    id))

(defn msg-text-raw [data] 
  (or (get-in data [:message :text])
      (get-in data [:callback_query :data])))

(defn msg-text [data]
  (let [t (msg-text-raw data)]
    (println "msg-text: " t)
    t))


