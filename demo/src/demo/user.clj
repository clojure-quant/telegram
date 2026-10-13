(ns demo.user
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [modular.permission.user.datahike :as user-db]))

(defn- slurp-edn-path
  "Load EDN from classpath or filesystem path."
  [path]
  (if-let [resource (io/resource path)]
    (slurp resource)
    (let [file (io/file path)]
      (when-not (.exists file)
        (throw (ex-info "users.edn not found"
                        {:path path
                         :absolute (.getAbsolutePath file)})))
      (slurp file))))

(defn load-edn-users
  [path]
  (let [data (edn/read-string (slurp-edn-path path))
        users (:users data)]
    (when-not (sequential? users)
      (throw (ex-info "users.edn must contain a :users vector" {:path path :users users})))
    users))

(defn seed-edn-users
  "Returns a db seed-fn that transacts users from an edn file."
  [path]
  (user-db/seed-users (load-edn-users path)))
