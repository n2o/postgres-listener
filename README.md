# postgres-listener

[![Clojars Project](https://img.shields.io/clojars/v/n2o/postgres-listener.svg)](https://clojars.org/n2o/postgres-listener)

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

## Installation

Download latest version from Clojars.
Download from http://example.com/FIXME.

## Usage

FIXME: explanation

    $ java -jar ai-connector-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
