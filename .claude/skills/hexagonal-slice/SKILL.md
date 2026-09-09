---
name: hexagonal-slice
description: Scaffolda en ny bounded context (feature-slice) i backend enligt Xelins hexagonala arkitektur — domain, application/port/in, application/port/out, application/service, infrastructure/web + persistence. Använd när en ny domän ska läggas till (ticket, queue, notification, audit, stats).
---

# Hexagonal slice

Xelin-backend är uppdelad i bounded contexts under `se.foi.xelin.<context>`. Referens: `se.foi.xelin.identity`.

## Paketstruktur (skapa exakt denna)

```
se.foi.xelin.<context>/
├── domain/
│   └── model/                 <Entity>.java            (ren Java, ingen Spring/JPA-import)
├── application/
│   ├── port/in/               <Verb><Substantiv>UseCase.java   ("Vad kan systemet göra?")
│   ├── port/out/              <Substantiv>Port.java            ("Vad behöver systemet från omvärlden?")
│   └── service/               <Substantiv>Service.java   @Service, implements UseCase
└── infrastructure/
    ├── web/                   <Context>Controller.java, <...>Request.java, <...>Response.java
    └── persistence/           <Entity>JpaEntity.java, <Entity>Repository.java (Spring Data), <...>PersistenceAdapter.java implements Port
```

## Regler

- **Beroenderiktning:** `infrastructure` → `application` → `domain`. Domänen importerar aldrig Spring, JPA eller något från `infrastructure`.
- **Portar är interfaces i `application`.** `port/in` implementeras av `service`. `port/out` implementeras av adapters i `infrastructure`.
- **Konstruktorinjektion**, inga fält-`@Autowired`, ingen Lombok (matchar `identity`).
- **Domänmodeller:** privata final-fält, konstruktor, getters. Ingen setter om värdet inte får ändras.
- **DTO:er** (`*Request`/`*Response`) ligger i `infrastructure/web`, aldrig i `application` eller `domain`. Mappning sker i controllern eller en dedikerad mapper.
- **JPA-entiteter** (`*JpaEntity`) ligger i `infrastructure/persistence` och är skilda från domänmodellen. Persistence-adaptern mappar mellan dem.
- Kommentarer på svenska, kort och förklarande — som i befintlig kod.

## Checklista

1. Skapa paketen ovan.
2. Domänmodell först — utan ramverk.
3. `port/in` UseCase-interface (en metod per use case).
4. `port/out` Port-interface för varje externt behov (DB, e-post, LDAP…).
5. `service` som implementerar UseCase och tar portar via konstruktor.
6. `infrastructure/persistence`: JpaEntity + Spring Data-repository + PersistenceAdapter (`implements` port/out).
7. `infrastructure/web`: Controller (se skill `rest-endpoint`) + Request/Response-DTO:er.
8. Om domänen ändrar tillstånd som ska spåras → emittera audit-händelse (se skill `audit-event`).
9. Schemat ägs av Flyway (se skill `flyway-migration`), inte av `ddl-auto`.
10. Tester: service med Mockito, controller med MockMvc.
