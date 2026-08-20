#!/bin/sh
set -e

# Render exposes Postgres as a libpq URL (postgresql://user:pass@host/db).
# The JDBC driver needs jdbc:postgresql://host/db with credentials passed
# separately, so translate it here unless DB_URL was set explicitly.
if [ -z "$DB_URL" ] && [ -n "$DATABASE_URL" ]; then
  DB_URL="jdbc:postgresql://${DATABASE_URL##*@}"
  export DB_URL
fi

echo "Starting InterviewOS on port ${PORT:-8080} against ${DB_URL%%\?*}"
exec java -jar /app/app.jar
