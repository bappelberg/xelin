---
name: rest-endpoint
description: Lägg till en REST-endpoint i Xelin-backend enligt projektets konventioner — controller i infrastructure/web, DTO:er, server-side behörighetskontroll per anrop, och felhantering som aldrig läcker stacktraces. Använd vid varje nytt API.
---

# REST-endpoint

Referens: `se.foi.xelin.identity.infrastructure.web.AuthController`.

## Placering

`se.foi.xelin.<context>.infrastructure.web`
- `<Context>Controller.java` — `@RestController`, `@RequestMapping("/api/<resurs>")`
- `<Handling>Request.java` / `<Handling>Response.java` — DTO:er, aldrig domän- eller JPA-objekt över tråden

## Regler

- **Controllern anropar bara `port/in` UseCase-interfaces**, aldrig services eller repositories direkt. Konstruktorinjektion.
- **REST/JSON** (KR-T104): substantiv i plural i path, HTTP-verb för handling, korrekta statuskoder (`201` + `Location` vid skapande, `204` vid radering, `200` annars).
- **Behörighet server-side per anrop** (KR-804): `@PreAuthorize` på metoden med roller `User` / `Agent` / `Admin`. Frontend-kontroller räknas inte som skydd. Kräver `@EnableMethodSecurity` (lägg i `WebSecurityConfig` om det saknas).
- **Validering:** `@Valid` på `@RequestBody`, Bean Validation-annoteringar på Request-DTO:n.
- **Inga interna felmeddelanden mot klient** (KR-802): ingen `e.getMessage()` från infrastruktur/DB rakt ut, inga stacktraces. Använd en central `@RestControllerAdvice` som mappar till RFC 7807 `ProblemDetail`:
  - domänfel (t.ex. `TicketNotFoundException`) → `404` / `409` med kort, ofarlig text
  - validering → `400`
  - allt oväntat → `500` med generisk text, full detalj endast i serverloggen
- **Audit:** tillståndsändrande anrop (skapa/tilldela/statusbyte/utloggning) ska resultera i en audit-händelse — helst i application-lagret, inte i controllern (se skill `audit-event`).
- **Loggning:** SLF4J, JSON i produktion (KR-T105). Logga aldrig lösenord eller LDAP-credentials (KR-805).

## Mall

```java
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final CreateTicketUseCase createTicket;

    public TicketController(CreateTicketUseCase createTicket) {
        this.createTicket = createTicket;
    }

    @PostMapping
    @PreAuthorize("hasRole('User')")
    public ResponseEntity<TicketResponse> create(
            @Valid @RequestBody CreateTicketRequest request,
            Authentication auth
    ) {
        Ticket ticket = createTicket.create(request.toCommand(auth.getName()));
        return ResponseEntity
                .created(URI.create("/api/tickets/" + ticket.getId()))
                .body(TicketResponse.from(ticket));
    }
}
```

## Checklista

1. Endpoint hör hemma i rätt bounded context.
2. UseCase finns i `port/in` — annars skapa det först.
3. Request/Response-DTO:er med validering.
4. `@PreAuthorize` med rätt roll.
5. Fel går via `@RestControllerAdvice` → `ProblemDetail`.
6. MockMvc-test: happy path + 401/403 + 400.
