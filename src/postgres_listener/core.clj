(ns postgres-listener.core
  "Connect to a postgres-database and wait for a NOTIFY of the database. This
  notify-event triggers a function, which receives the payload as EDN if the
  payload was initially encoded as JSON (for example with postgres' function
  \"row_to_json()\"."
  (:require [clojure.data.json :as json]
            [clojure.walk :refer [keywordize-keys]])
  (:import [com.impossibl.postgres.jdbc PGDataSource]
           [com.impossibl.postgres.api.jdbc PGNotificationListener])
  (:gen-class))

;; -----------------------------------------------------------------------------
;; Auxiliary Functions

(defn- json->edn
  "Try to parse payload. Return EDN if payload is json. Else returns
   string-representation."
  [payload]
  (try
    (keywordize-keys (json/read-str payload))
    (catch Exception e
      payload)))


;; -----------------------------------------------------------------------------

(def ^:private datasource (atom nil))

(defn- make-listener
  "Takes a function f/1 and returns a listener which is automatically triggered
  when the specified event occurs."
  [f]
  (reify PGNotificationListener
    (^void notification [this ^int processId ^String channelName ^String payload]
     (f (json->edn payload)))))

(defn- connection
  "Establishes connection to database. datasource needs to be defined."
  [f]
  (doto (.getConnection @datasource)
    (.addNotificationListener (make-listener f))))

(defn arm-listener
  "Creates listener for new events in the eventstore. f needs to be a function,
  which gets one parameter, which is the (transformed) payload published by
  postgres' trigger.

  For example:
  (arm-listener (fn [payload] (println payload)) \"LISTEN new_event;\")"
  [f event]
  (doto (.createStatement (connection f))
    (.execute event)
    (.close)))

(defn connect [{:keys [host port database user password]}]
  (reset! datasource (doto (PGDataSource.)
                       (.setHost host)
                       (.setPort port)
                       (.setDatabase database)
                       (.setUser user)
                       (.setPassword password))))


(comment
  (connect {:host "localhost" :port 5432 :database "discussion" :user "postgres" :password "postgres"})
  (arm-listener (fn [payload] (println payload)) "LISTEN new_event;")
  )


;; -----------------------------------------------------------------------------
;; Testing

(require '[korma.core :refer :all]
         '[korma.db :refer :all])

(defdb pg (postgres
           {:host "localhost"
            :port "5432"
            :db "discussion"
            :user "postgres"
            :password "postgres"
            :delimiters ""}))

(defentity issues
  (entity-fields :uid :title :slug :info))

(select issues)
(insert issues (values {:title "foo" :info "bar" :long_info "foo" :slug (-> (rand-int 999999) str) :is_disabled false}))

(rand-int 100)

(-> (new java.util.HashMap)
  (.put "a" 1)
  (println))
