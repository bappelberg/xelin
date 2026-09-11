# Xelin — ITSM/helpdesk för FOI

Internt ärendehanterings- och helpdesksystem. Fullständig kravspec: `REQUIREMENTS.md` (krav-ID `KR-xxx` / `KR-Txxx`). Systemet hanterar **inte** klassificerad eller sekretessbelagd information.

## Stack

| Lager    | Teknik |
|----------|--------|
| Backend  | Java 21, Spring Boot 3.5, Maven, hexagonal arkitektur |
| Auth     | Spring Security + Spring LDAP mot OpenLDAP, server-side sessioner (JSESSIONID), **ingen JWT** (KR-103) |
| DB       | PostgreSQL 17, schema via versionshanterade SQL-skript (`ddl-auto` ska vara `none`) |
| Frontend | Next.js 16, React 19, TypeScript, Tailwind v4 |
| Drift    | Docker Compose; Nginx som reverse proxy + TLS i produktion |
| Infra    | Terraform i `infra/terraform/` |

## Backend-arkitektur

Bounded contexts under `se.foi.xelin.<context>`. Hittills bara `identity` (LDAP-inloggning). Varje context:

```
domain/model            — ren Java, inga ramverk
application/port/in      — <Verb><Substantiv>UseCase   (systemets förmågor)
application/port/out     — <Substantiv>Port            (behov från omvärlden)
application/service      — @Service implements UseCase
infrastructure/web       — Controller + Request/Response-DTO:er
infrastructure/persistence — JpaEntity + Spring Data-repo + PersistenceAdapter
```

Beroenderiktning: `infrastructure` → `application` → `domain`. Konstruktorinjektion, ingen Lombok. Kommentarer på svenska. Se skill `hexagonal-slice`.

## Skills (`.claude/skills/`)

- **hexagonal-slice** — scaffolda en ny bounded context
- **dev-env** — kör upp stacken, LDAP-testanvändare, verifiera login
- **rest-endpoint** — nya API:er (authz per anrop, ProblemDetail-fel)
- **audit-event** — skriva till granskningsloggen

## Kör lokalt

`.env` i repo-roten måste ha `DB_PASSWORD`. Sedan `docker compose up --build`.
Portar: frontend 3000, backend 8080, db 5432, openldap 389/636.
Testanvändare: `ben` / `benspassword`, `bob` / `bobspassword` (bas-DN `dc=foi,dc=se`, `ou=people`).

## Konventioner som gäller överallt

- Behörighetskontroll **server-side för varje API-anrop** (KR-804). Roller: `User`, `Agent`, `Admin`, härledda från LDAP-grupper (KR-102).
- Inga stacktraces eller interna felmeddelanden mot klienten (KR-802).
- Lösenord / LDAP-credentials loggas eller lagras aldrig i klartext (KR-805).
- Ingen hårdkodning av host/port/credentials — miljövariabler + Spring-profiler (KR-904).
- Alla schemaändringar via versionshanterade SQL-skript i repot (KR-902) — inga odokumenterade ad hoc-ändringar i produktion.
- Strukturerad loggning (SLF4J), JSON i produktion (KR-T105).
- Frontend pratar bara med REST-API:et (KR-T203).

## Kända glapp (att åtgärda medvetet, inte kopiera)

- `application.yaml` finns i **två** varianter (repo-roten + `backend/src/main/resources/`) med olika LDAP-URL. Resurs-varianten med `${LDAP_URL:...}` är den som används; root-filen har `ddl-auto: update` och bör tas bort (ska vara `none`, se ovan).
- Frontend hårdkodar `http://localhost:8080` i stället för proxy/miljövariabel.
- `frontend/src/app/layout.tsx` har kvar `create-next-app`-metadata.
- `frontend/src/app/login/page.tsx` refererar `/foi-vapen.png` (finns inte; filen heter `foi-weapon.png`).

## Frontend

Egna regler i `frontend/AGENTS.md` — Next.js 16 har breaking changes; läs `node_modules/next/dist/docs/` innan du skriver kod. Route-skydd sker i `frontend/src/proxy.ts` (Next 16: middleware → proxy).
