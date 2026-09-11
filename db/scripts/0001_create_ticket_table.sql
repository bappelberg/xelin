-- KR-902/KR-T302: schemat hanteras via versionshanterade SQL-skript, inte av Hibernate (ddl-auto: none) eller ett migreringsverktyg.
-- Körs automatiskt av postgres-imagen vid första uppstart mot en tom volym (docker-entrypoint-initdb.d),
-- annars manuellt via db/setup.sh eller db/scripts/<fil> mot en redan befintlig databas.

CREATE TABLE ticket (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title        VARCHAR(200) NOT NULL,
    description  TEXT NOT NULL,
    priority     VARCHAR(20) NOT NULL
                     CHECK (priority IN ('LOW', 'NORMAL', 'HIGH', 'CRITICAL')),
    category     VARCHAR(20) NOT NULL
                     CHECK (category IN ('HARDWARE', 'SOFTWARE', 'ACCOUNT', 'NETWORK', 'OTHER')),
    status       VARCHAR(20) NOT NULL
                     CHECK (status IN ('NEW', 'ASSIGNED', 'IN_PROGRESS', 'PENDING', 'RESOLVED', 'CLOSED')),
    reporter     VARCHAR(100) NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL
);
