(ns demo
  (:require
   [clojure.pprint :refer [print-table]]
   [telegrambot-lib.core :as tbot]
   [telegram.command :as cmd]))

(defn telegram-demo [bot]
  (println "demo starting..")
  (let [r1 (tbot/get-me bot)
        r5 (tbot/get-my-commands bot)]
    (println "get-me: " r1)
    (println "get-my-commands result: " r5)))


(defn md1 [text]
  {:md (slurp "asset/demo.md")})

(defn wink [text]
  {:html (slurp "asset/demo.html")})

(def data [{:age 15 :name "Willy" :likes "Banana"}
           {:age 18 :name "Anna" :likes "Beach"}
           {:age 88 :name "Abuela" :likes "Brownies"}
           {:age 0 :name "TelegramBotLib" :likes "Bowling"}])



(defn table1 [text]
  {:md (with-out-str (print-table data))})

(defn table2 [text]
  {:html (str  "<code>"
               (with-out-str (print-table data))
               "</code>")})

(def data3 [{:age 15 :name "Willy" :likes "Banana" :icon "😀"}
            {:age 18 :name "Anna" :likes "Beach" :icon "😛"}
            {:age 88 :name "Abuela" :likes "Brownies" :icon ""}
            {:age 0 :name "TelegramBotLib" :likes "Bowling" :icon "X"}])



(defn table3 [text]
  {:html (str  "<code>"
               (with-out-str (print-table data3))
               "</code>")})


(defn time [text]
  {:text (str "time 08:08:08 text:" text)
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


(defn app [text]
  {:text "Test web_app"
   :web {:url "https://revenkroz.github.io/telegram-web-app-bot-example/index.html"}
   :keyboard  [[{:text "app"  :web_app {:url "https://revenkroz.github.io/telegram-web-app-bot-example/index.html"}}]]})



(defn dialog1 [text]
  {:text (str "set options" text)
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

(defn moon [text]
  {:photo "https://www.virtualtelescope.eu/wordpress/wp-content/uploads/2014/03/moon_16mar2014_stretched.jpg"})