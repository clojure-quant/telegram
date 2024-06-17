(ns demo
  (:require
   [clojure.pprint :refer [print-table]]
   [telegrambot-lib.core :as tbot]
   [telegram.pubsub :refer [publish]]))

;; RPC COMMANDS

(defn md1 [_]
  {:md (slurp "asset/demo.md")})

(defn wink [_]
  {:html (slurp "asset/demo.html")})

(def data [{:age 15 :name "Willy" :likes "Banana"}
           {:age 18 :name "Anna" :likes "Beach"}
           {:age 88 :name "Abuela" :likes "Brownies"}
           {:age 0 :name "TelegramBotLib" :likes "Bowling"}])



(defn table1 [_]
  {:md (with-out-str (print-table data))})

(defn table2 [_]
  {:html (str  "<code>"
               (with-out-str (print-table data))
               "</code>")})

(def data3 [{:age 15 :name "Willy" :likes "Banana" :icon "😀"}
            {:age 18 :name "Anna" :likes "Beach" :icon "😛"}
            {:age 88 :name "Abuela" :likes "Brownies" :icon ""}
            {:age 0 :name "TelegramBotLib" :likes "Bowling" :icon "X"}])

(defn table3 [_]
  {:html (str  "<code>"
               (with-out-str (print-table data3))
               "</code>")})


(defn timezone [_ timezone]
  {:text (str "timezone is now set to: " timezone)})


(defn app [_]
  {:text "Test web_app"
   :web {:url "https://revenkroz.github.io/telegram-web-app-bot-example/index.html"}
   :keyboard  [[{:text "app"  :web_app {:url "https://revenkroz.github.io/telegram-web-app-bot-example/index.html"}}]]})



(defn dialog1 [_]
  {:text "reply keyboard:"
   :reply-keyboard  [[{:text "UTC" :callback_data "btn_utc"}
                      {:text "EST" :callback_data "btn_est"}]
                     [{:text "1" :callback_data "btn_1"}
                      {:text "2" :callback_data "btn_2"}
                      {:text "3" :callback_data "btn_3"}
                      {:text "4" :callback_data "btn_4"}
                      {:text "5" :callback_data "btn_5"}
                      {:text "6" :callback_data "btn_6"}
                      {:text "7" :callback_data "btn_7"}
                      {:text "8" :callback_data "btn_8"}
                      {:text "9" :callback_data "btn_9"}]]})

(defn dialog2 [_]
  {:text "full keyboard: "
   :keyboard  [[{:text "UTC" :callback_data "btn_utc"}
                {:text "EST" :callback_data "btn_est"}]
               [{:text "winter" :callback_data "btn_winter"}
                {:text "summer" :callback_data "btn_summer"}]
               [{:text "1" :callback_data "btn_1"}
                {:text "2" :callback_data "btn_2"}
                {:text "3" :callback_data "btn_3"}
                {:text "4" :callback_data "btn_4"}
                {:text "5" :callback_data "btn_5"}
                {:text "6" :callback_data "btn_6"}
                {:text "7" :callback_data "btn_7"}
                {:text "8" :callback_data "btn_8"}
                {:text "9" :callback_data "btn_9"}]]})

(defn moon [_]
  {:photo "https://www.virtualtelescope.eu/wordpress/wp-content/uploads/2014/03/moon_16mar2014_stretched.jpg"})

;; DEMO SERVICE

(defn telegram-demo [{:keys [bot state]}]
  (println "demo starting..")
  (let [r1 (tbot/get-me bot)
        r5 (tbot/get-my-commands bot)]
    (println "get-me: " r1)
    (println "get-my-commands result: " r5)))

;; DATA PUBLISHER

(defn random-data []
  {:html (str "topic: default, random data: " (rand-int 100))})

(defn start-data-pusher [this]
  (loop []
    (let [topic "marketdata"
          msg (random-data)]
      (println "publishing topic: marketdata data: " msg)
      (publish this topic msg)
      (Thread/sleep (* 5 60 1000)) ; 5 min
      (recur))))

