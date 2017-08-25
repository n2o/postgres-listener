# postgres-listener

[![Clojars Project](https://img.shields.io/clojars/v/de.hhu.cn/postgres-listener.svg)](https://clojars.org/de.hhu.cn/postgres-listener)
[![Build Status](https://travis-ci.org/n2o/postgres-listener.svg?branch=master)](https://travis-ci.org/n2o/postgres-listener)

Connect to a postgres-database and wait for a NOTIFY of the database. This
notify-event triggers a function, which receives the payload as EDN if the
payload was initially encoded as JSON (for example with postgres' function
`row_to_json()`.

A trigger for a table 'events' can be configured like this:

    // language: PLpgSQL
    -- Define function for notify-event
    CREATE OR REPLACE FUNCTION PUBLIC.NOTIFY() RETURNS trigger AS
    $BODY$
    BEGIN
    PERFORM pg_notify('new_event', row_to_json(NEW)::text);
    RETURN new;
    END;
    $BODY$
    LANGUAGE 'plpgsql' VOLATILE COST 100;)

    -- Creates trigger and binds it to \"events\"-table
    CREATE TRIGGER new_event
    AFTER INSERT
    ON events
    FOR EACH ROW
    EXECUTE PROCEDURE PUBLIC.NOTIFY();

## Usage

Require latest version of postgres-listener in your project.clj (check latest
version in the badge as this string is very likely to be forgotten on new
releases):

```clojure
:dependencies [[de.hhu.cn/postgres-listener "0.1.0"]
```

You can then use it in your code to connect to the database, which provides the
NOTIFY channel and add a listener to the corresponding LISTEN event. If your
table is configured as shown above, you can react to the trigger `new_event`
like this:

```clojure
(require '[postgres-listener.core :as pgl])

(pgl/connect {:host "localhost" :port 5432 :database "postgres" :user "postgres" :password "postgres"})
(pgl/arm-listener (fn [payload] (println payload)) "new_event")
```

## License

Copyright © 2017 Christian Meter

Distributed under the [MIT license](https://choosealicense.com/licenses/mit/).
