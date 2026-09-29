-- KR-208: kommentarer på ärenden. Interna kommentarer är bara synliga för
-- handläggare och administratörer (filtreras i application-lagret, inte här).
-- KR-902/KR-T302: versionshanterat SQL-skript, inte Hibernate (ddl-auto: none).

CREATE TABLE ticket_comment (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ticket_id    BIGINT NOT NULL REFERENCES ticket (id),
    author       VARCHAR(100) NOT NULL,
    body         TEXT NOT NULL,
    internal     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMPTZ NOT NULL
);

CREATE INDEX ticket_comment_ticket_id_idx ON ticket_comment (ticket_id);
