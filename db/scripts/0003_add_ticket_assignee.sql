-- KR-204: handläggare kan tilldelas ett ärende (sig själva eller en kollega).
-- KR-902/KR-T302: versionshanterat SQL-skript, inte Hibernate (ddl-auto: none).

ALTER TABLE ticket ADD COLUMN assignee VARCHAR(100);
