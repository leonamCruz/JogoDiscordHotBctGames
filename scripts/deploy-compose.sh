#!/usr/bin/env bash
set -euo pipefail

if [[ ! -f "env.env" ]]; then
  echo "Arquivo env.env nao encontrado. Crie a partir de .env.example."
  exit 1
fi

docker compose up -d --build
docker compose ps
