(defproject de.hhu.cn/postgres-listener "0.2.0"
  :description "Connect to a postgres-database and wait for a NOTIFY event to trigger a function"
  :url "https://github.com/n2o/postgres-listener"
  :license {:name "MIT"
            :url "https://choosealicense.com/licenses/mit/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [com.impossibl.pgjdbc-ng/pgjdbc-ng "0.7.1"]
                 [org.clojure/data.json "0.2.6"]
                 [com.taoensso/timbre "4.10.0"]]
  :main ^:skip-aot postgres-listener.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
