---
name: dev-env
description: Kör upp Xelin lokalt (Docker Compose) och verifiera att inloggning fungerar. Använd när något ska testas mot en körande stack, eller vid felsökning av login/LDAP/CORS/sessioner.
---

# Dev-miljö

## Starta

```bash
# Kräver .env i repo-roten med DB_PASSWORD satt
docker compose up --build
```

Tjänster och portar:

| Tjänst    | Port(ar)      | Not |
|-----------|---------------|-----|
| frontend  | 3000          | Next.js dev, bind-mountad från `./frontend` |
| backend   | 8080          | Spring Boot, `/api/...` |
| db        | 5432          | postgres:17, volym `xelin_pgdata` |
| openldap  | 389, 636      | osixia/openldap, service-namn `openldap`, hostname `ldap.foi.se` |

## LDAP-testanvändare (från `ldap/bootstrap.ldif`)

| uid | lösenord       | cn            |
|-----|----------------|---------------|
| ben | `benspassword` | Ben Alex      |
| bob | `bobspassword` | Bob Hamilton  |

Bas-DN `dc=foi,dc=se`, användare under `ou=people`. Admin-bind: `cn=admin,dc=foi,dc=se` / `admin`.
Lösenord lagras BCrypt-hashade i `userPassword` och jämförs av `LdapPasswordComparisonAuthenticationManagerFactory`.

## Verifiera inloggning

```bash
curl -i -c /tmp/xelin.cookies \
  -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"ben","password":"benspassword"}'
# Förväntat: 200 + body "ben" + Set-Cookie: JSESSIONID=...

curl -i -b /tmp/xelin.cookies -X POST http://localhost:8080/api/auth/logout
# Förväntat: 200
```

Kontrollera LDAP direkt:

```bash
docker compose exec openldap ldapsearch -x -H ldap://localhost \
  -b "dc=foi,dc=se" -D "cn=admin,dc=foi,dc=se" -w admin "(uid=ben)"
```

## Frontend

- Login: `http://localhost:3000/login`. Vänsterpanelen med FOI-vapnet har `hidden lg:flex` — **visas bara när fönstret är ≥1024px brett**.
- Skyddade sidor gate:as av `frontend/src/proxy.ts` (Next 16 döpte om middleware → proxy): saknas `JSESSIONID` på `/dashboard/*` → redirect till `/login`.
- Frontend anropar just nu `http://localhost:8080` hårdkodat. CORS tillåts explicit för `http://localhost:3000` i `WebSecurityConfig` med `allowCredentials(true)`.

## Vanliga fel

- **401 på korrekt lösenord:** kontrollera att `bootstrap.ldif` importerats (loggar från openldap-containern) och att `userPassword` är BCrypt.
- **CORS-fel i browsern:** origin måste vara exakt `http://localhost:3000`; `fetch` måste ha `credentials: "include"`.
- **Ny tom session efter utloggning:** hanteras av `SessionCreationPolicy.IF_REQUIRED` i `WebSecurityConfig`.
- **`DB_PASSWORD` saknas:** compose failar tyst på db/backend — kolla `.env`.
