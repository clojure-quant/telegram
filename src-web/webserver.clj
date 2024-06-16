(ns telegram.webserver
  (:require
   [modular.webserver.jetty :refer [run-jetty-server]]
   [modular.webserver.handler.not-found :refer [not-found-handler]]
   [modular.webserver.handler.files :refer [->FilesMaybe ->ResourcesMaybe]]
   [modular.webserver.middleware.bidi :refer [wrap-bidi]]
   [modular.webserver.middleware.exception :refer [wrap-fallback-exception]]
   [modular.webserver.middleware.api :refer [wrap-api-handler]]
   [modular.webserver.page :refer [page]]
   [telegram.hook :refer [telegram-hook-handler-wrapped]]
   ))

(defn main-page [_]
  (page {:title "demo-123"
         :author "goblin77"}
        [:div
         [:h1 "hello, world!"]
         [:a {:href "/r/demo.txt"} [:p "demo.txt"]]
         [:a {:href "/big-void"} [:p "big-void (unknown route)"]]]))

(def routes
  ["/" {"" main-page
        "api/telegram" telegram-hook-handler-wrapped
        "r/" (->ResourcesMaybe {:prefix "public"})
        #{"r" "public"} (->FilesMaybe {:dir "src-demo/public"})
        true not-found-handler}])

(defn run-webserver [& _]
  (let [ring-handler (-> (wrap-bidi routes)
                         (wrap-fallback-exception))]
    (run-jetty-server ring-handler nil
                      {:port 8081
                       :host "0.0.0.0"
                       :join? false ; true
                       ;:ssl-port 8443
                       ;:keystore "./certs/keystore.p12"
                       ;:key-password "password"; Password you gave when creating the keystore
                       })))
;; https://danielflower.github.io/2017/04/08/Lets-Encrypt-Certs-with-embedded-Jetty.html

