(ns repl
   (:require
    [modular.system]
    [telegram.pubsub :as pubsub]
    [telegram.command :as cmd]
    ))

(def t (modular.system/system :telegram))

t

(pubsub/subscriptions t)

; marketdata

(pubsub/topic-subscribers t "marketdata")

(pubsub/publish t "marketdata" {:html "marketdata publish message demo."})

; unknown topic

(pubsub/topic-subscribers t "unknown3")

; add a new subscription

(def chat-id 1368217878)

(pubsub/subscribe 
 (pubsub/add-current-session t {:chat-id chat-id})
 "bongo3")

(pubsub/topic-subscribers t "bongo3")

(pubsub/publish t "bongo3" {:html "topic: bongo3: bongo is 3 years old!"})

;

(cmd/valid-commands? (:commands t))


(cmd/valid-commands? [; valid here:
                      {:command "asdf"}
                      {:command "asdfasdf"}
                      ; non valid here:
                      ;{:command nil}
                      {:command "adsflk; asdklfj asdlkf lakdsjf lkasdjf lkasdf jlaksdjf a;lksdf "}
                      ])




