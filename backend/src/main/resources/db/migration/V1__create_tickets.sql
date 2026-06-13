-- LAGER: Databas – Flyway kör denna en gång och versionshanterar schemat (KR-902).
-- Manuella ändringar i produktion är inte tillåtna; skapa alltid ny V*-fil istället.

CREATE TABLE tickets (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(200)  NOT NULL,
    description VARCHAR(2000) NOT NULL,
    status      VARCHAR(50)   NOT NULL DEFAULT 'NY',
    created_at  TIMESTAMPTZ   NOT NULL,
    updated_at  TIMESTAMPTZ   NOT NULL
);
