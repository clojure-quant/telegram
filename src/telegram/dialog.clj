(ns telegram.dialog)

(defn str->button [s]
  {:text s :callback_data s})

(defn keyboard-options [options]
  #_[[{:text "UTC" :callback_data "btn_utc"}
   {:text "EST" :callback_data "btn_est"}] ]
  [(->> (map str->button options)
       (into []))])

