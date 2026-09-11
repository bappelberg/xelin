#!/usr/bin/env bash
# Kör alla schema-skript i db/scripts/ i filnamnsordning mot databasen.
# KR-902/KR-T302: versionshanterade SQL-skript i repot, inget migreringsverktyg.
#
# Vid en helt NY volym körs samma skript redan automatiskt av postgres-imagen
# (se docker-entrypoint-initdb.d-monteringen i docker-compose.yml) — det här
# skriptet behövs då inte. Använd det i stället när databasen redan finns och
# du lagt till ett nytt numrerat skript: kör bara det skriptet direkt, t.ex.
#   docker compose exec -T db psql -U xelin -d xelin < db/scripts/0002_xxx.sql
# Kör inte om hela mappen mot en databas som redan har tabellerna —
# CREATE TABLE kraschar då på det som redan finns.
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

for f in "$repo_root"/db/scripts/*.sql; do
  echo "==> $(basename "$f")"
  docker compose exec -T db psql -U xelin -d xelin -v ON_ERROR_STOP=1 -f - < "$f"
done
