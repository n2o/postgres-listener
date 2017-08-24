(defproject postgres-listener "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "MIT"
            :url "https://choosealicense.com/licenses/mit/"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [korma "0.4.3"]
                 [org.postgresql/postgresql "42.1.4"]
                 [com.impossibl.pgjdbc-ng/pgjdbc-ng "0.7"]
                 [org.clojure/data.json "0.2.6"]]
  :main ^:skip-aot postgres-listener.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
