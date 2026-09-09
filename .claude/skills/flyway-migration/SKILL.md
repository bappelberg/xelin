---
name: flyway-migration
description: Skapa en databasmigrering för Xelin med Flyway. Använd vid varje schemaändring — nya tabeller, kolumner, index, constraints, seed-data. KR-902 / KR-T302 kräver Flyway och förbjuder manuella schemaändringar.
---

# Flyway-migrering

## Var

`backend/src/main/resources/db/migration/`

## Namnkonvention

```
V<n>__<kort_beskrivning>.sql      t.ex. V3__add_ticket_status_index.sql
```

- `V` versaler, dubbelt understreck `__` mellan nummer och beskrivning.
- `<n>` är löpande heltal, aldrig återanvänt. Kolla högsta befintliga numret först.
- Beskrivning i snake_case, imperativ, kort.
- Repeatable migreringar (`R__...`) endast för vyer/funktioner som får skrivas om.

## Regler

- **Ändra aldrig en redan committad/applicerad migrering.** Rätta genom en ny migrering.
- En migrering = en logisk ändring. Blanda inte orelaterade ändringar.
- Explicita constraints och `NOT NULL` från början. Namnge constraints (`fk_ticket_assignee`, `chk_ticket_status`).
- Ingen data i klartext som är känslig (KR-T303).
- DDL först, sedan ev. data-backfill i samma eller efterföljande migrering.
- Testa mot en ren db: `docker compose down -v && docker compose up`.

## Setup (om Flyway ännu inte är inkopplat)

1. `pom.xml`: lägg till `org.flywaydb:flyway-core` och `flyway-database-postgresql`.
2. `application.yaml`: sätt `spring.jpa.hibernate.ddl-auto: validate` (aldrig `update`/`create` — schemat ägs av Flyway). Ta bort/ersätt root-`application.yaml` som har `ddl-auto: update`.
3. `spring.flyway.enabled: true` (default när beroendet finns).
4. Baseline: `V1__init.sql` med nuvarande schema.

## Mall

```sql
-- V<n>__<beskrivning>.sql
-- Kravref: KR-2xx

CREATE TABLE ticket (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title         TEXT        NOT NULL,
    status        TEXT        NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_ticket_status CHECK (status IN ('NY','TILLDELAD','PAGAENDE','VANTANDE','LOST','STANGD'))
);

CREATE INDEX idx_ticket_status ON ticket (status);
```
