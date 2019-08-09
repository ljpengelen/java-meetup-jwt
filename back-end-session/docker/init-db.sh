#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE java_meetup;
    GRANT ALL PRIVILEGES ON DATABASE java_meetup TO "$POSTGRES_USER";
EOSQL
