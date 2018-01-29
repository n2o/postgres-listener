(ns postgres-listener.core
  "Connect to a postgres-database and wait for a NOTIFY of the database. This
  notify-event triggers a function, which receives the payload as EDN if the
  payload was initially encoded as JSON (for example with postgres' function
  \"row_to_json()\"."
  (:require [clojure.data.json :as json]
            [clojure.walk :refer [keywordize-keys]]
            [taoensso.timbre :as log])
  (:import [com.impossibl.postgres.jdbc PGDataSource]
           [com.impossibl.postgres.api.jdbc PGNotificationListener]))

;; -----------------------------------------------------------------------------
;; Auxiliary Functions

(defn- json->edn
  "Try to parse payload. Return EDN if payload is json. Else return
   string as provided by postgres."
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
  "Establishes connection to database. datasource needs to be defined.

  For example:
  (connect {:host \"localhost\" :port 5432 :database \"postgres\" :user \"postgres\" :password \"postgres\"})"
  [f]
  (try
    (doto (.getConnection @datasource)
      (.addNotificationListener (make-listener f)))
    (catch Exception e
      (log/error "No connection to database: " e))))

(defn arm-listener
  "Creates listener for new events in the eventstore. f/1 needs to be a
  function, which gets one parameter, which is the (transformed) payload
  published by postgres' trigger. And the event must match with the name
  of the trigger, e.g. \"new_event\".

  For example:
  (arm-listener (fn [payload] (println payload)) \"new_event\")"
  [f event]
  (doto (.createStatement (connection f))
    (.execute (format "LISTEN %s;" event))
    #_(.close)))

(defn connect [{:keys [host port database user password]}]
  (reset! datasource (doto (PGDataSource.)
                       (.setHost host)
                       (.setPort port)
                       (.setDatabase database)
                       (.setUser user)
                       (.setPassword password))))


(defn update-issues [payload]
  (println "New issue!" payload))
(defn update-textversion [payload]
  (println "New textversion!" payload))

(comment
  (connect {:host "localhost" :port 5432 :database "discussion" :user "postgres" :password "DXxCNtfnt!MOo!f8LY1!P%sw3KGzt@s!"})
  (arm-listener (fn [payload] (println payload)) "statements_changes")
  (arm-listener update-issues "issues_changes")
  (arm-listener update-textversion "textversions_changes")
  )
