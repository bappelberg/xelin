---
name: audit-event
description: Emittera en granskningsloggpost (audit event) i Xelin på ett konsekvent sätt. Använd vid inloggning/utloggning (KR-105), ärendestatusändringar och tilldelning (KR-205), och alla systemhändelser som ska synas i granskningsloggen (KR-503).
---

# Audit-händelse

Granskningsloggen är en egen bounded context: `se.foi.xelin.audit`. Andra contexts skriver till den via dess `port/in`, aldrig genom att röra dess tabell direkt.

## Vad varje post måste innehålla

- `timestamp` (`TIMESTAMPTZ`, server-tid)
- `actorUsername` — vem som utförde handlingen (uid från LDAP), eller `SYSTEM`
- `action` — enum/konstant, t.ex. `LOGIN_SUCCESS`, `LOGIN_FAILURE`, `LOGOUT`, `TICKET_CREATED`, `TICKET_STATUS_CHANGED`, `TICKET_ASSIGNED`
- `targetType` + `targetId` — vad handlingen gällde (t.ex. `TICKET` / `1042`), eller null
- `detail` — kort strukturerad text/JSON, t.ex. `{"from":"NY","to":"TILLDELAD"}`. **Aldrig lösenord, LDAP-credentials eller känsligt innehåll** (KR-805).

## Hur man emitterar

1. `audit` context exponerar:
   ```java
   // se.foi.xelin.audit.application.port.in
   public interface RecordAuditEventUseCase {
       void record(AuditEvent event);
   }
   ```
2. Anropande context injicerar `RecordAuditEventUseCase` i sin **application-service** (inte i controllern) och anropar den efter att handlingen lyckats.
3. `audit` har en `port/out` `AuditEventRepository` med en persistence-adapter (append-only tabell).

## Regler

- **Append-only.** Ingen update eller delete på audit-poster.
- Audit-skrivning får inte krascha huvudflödet: om loggningen failar, logga felet via SLF4J men låt handlingen stå kvar (eller kör i samma transaktion om kravet är strikt spårbarhet — välj medvetet per händelse).
- Emittera **efter** att handlingen bekräftats, med faktiskt utfall (`LOGIN_FAILURE` loggas också — KR-105).
- Läsning av granskningsloggen: endast `Admin` (KR-503), via egen endpoint i `audit.infrastructure.web` med `@PreAuthorize("hasRole('Admin')")`.
- Tidsstämpel och actor sätts centralt i `audit`, inte av varje anropare (skicka in actor, låt `audit` sätta timestamp).

## Exempel

```java
// i AuthController-flödet eller en AuthenticationService
auditEvents.record(AuditEvent.of(
        username,
        AuditAction.LOGIN_SUCCESS,
        null, null,
        null));
```

Se även skill `hexagonal-slice` (strukturen för `audit`) och `flyway-migration` (audit-tabellen).
