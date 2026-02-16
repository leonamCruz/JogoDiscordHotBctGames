#!/usr/bin/env sh
set -e

if [ -z "${DATABASE_URL:-}" ]; then
  exec "$@"
fi

HOST=$(echo "$DATABASE_URL" | sed -n 's|jdbc:mariadb://\([^:/]*\).*|\1|p')
PORT=$(echo "$DATABASE_URL" | sed -n 's|jdbc:mariadb://[^:/]*:\([0-9]*\).*|\1|p')
if [ -z "$HOST" ]; then
  HOST="mariadb"
fi
if [ -z "$PORT" ]; then
  PORT="3306"
fi

echo "Aguardando MariaDB em ${HOST}:${PORT}..."
for i in $(seq 1 60); do
  if nc -z "$HOST" "$PORT"; then
    echo "MariaDB pronto."
    exec "$@"
  fi
  sleep 2
done

echo "MariaDB nao respondeu a tempo."
exit 1
