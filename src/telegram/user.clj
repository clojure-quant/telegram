(ns telegram.user
  (:require
   [taoensso.timbre :refer [info warn]]
   [datahike.api :as d]))

(def schema
  [{:db/ident :user/telegram-token
    :db/unique :db.unique/identity
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :telegram/uid
    :db/unique :db.unique/identity
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}
   {:db/ident :telegram/user
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}])

(defn- find-user-eid-by-token [db token]
  (d/q '[:find ?e .
         :in $ ?token
         :where [?e :user/telegram-token ?token]]
       db token))

(defn- find-link-eid [db uid]
  (d/q '[:find ?e .
         :in $ ?uid
         :where [?e :telegram/uid ?uid]]
       db uid))

(defn- pull-user [db user-eid]
  (when user-eid
    (let [u (d/pull db '[*] user-eid)]
      (cond-> u
        (contains? u :user/email) (update :user/email #(vec (or % [])))
        (contains? u :user/roles) (update :user/roles #(into #{} (or % [])))))))

(defn get-user [db uid]
  (let [db-val @db
        uid (long uid)
        user-eid (d/q '[:find ?u .
                        :in $ ?uid
                        :where
                        [?e :telegram/uid ?uid]
                        [?e :telegram/user ?u]]
                      db-val uid)]
    (pull-user db-val user-eid)))

(defn connect [db uid token]
  (let [db-val @db
        uid (long uid)
        token (str token)
        user-eid (find-user-eid-by-token db-val token)]
    (if-not user-eid
      (do (warn "telegram connect: unknown token")
          nil)
      (do (d/transact db [{:telegram/uid uid
                           :telegram/user user-eid}])
          (info "telegram connect: uid" uid "user-eid" user-eid)
          (get-user db uid)))))

(defn disconnect [db uid]
  (let [db-val @db
        uid (long uid)
        eid (find-link-eid db-val uid)]
    (if-not eid
      (do (warn "telegram disconnect: unknown uid" uid)
          nil)
      (do (d/transact db [[:db/retract eid :telegram/uid]
                          [:db/retract eid :telegram/user]])
          (info "telegram disconnect: uid" uid)
          true))))
