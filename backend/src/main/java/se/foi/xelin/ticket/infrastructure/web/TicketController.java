package se.foi.xelin.ticket.infrastructure.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.foi.xelin.ticket.application.port.in.CreateTicketUseCase;
import se.foi.xelin.ticket.application.port.in.GetTicketUseCase;
import se.foi.xelin.ticket.application.port.in.ListTicketsUseCase;
import se.foi.xelin.ticket.application.port.in.UpdateTicketUseCase;
import se.foi.xelin.ticket.domain.model.Ticket;
import se.foi.xelin.ticket.domain.model.TicketNotFoundException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final CreateTicketUseCase createTicket;
    private final ListTicketsUseCase listTickets;
    private final GetTicketUseCase getTicket;
    private final UpdateTicketUseCase updateTicket;

    public TicketController(CreateTicketUseCase createTicket, ListTicketsUseCase listTickets,
                            GetTicketUseCase getTicket, UpdateTicketUseCase updateTicket) {
        this.createTicket = createTicket;
        this.listTickets = listTickets;
        this.getTicket = getTicket;
        this.updateTicket = updateTicket;
    }

    // KR-201: slutanvändare registrerar ett ärende.
    // Behörighet kontrolleras server-side per anrop (KR-804); rollen härleds från LDAP-grupp (KR-102).
    @PostMapping
    @PreAuthorize("hasRole('User')")
    public ResponseEntity<TicketResponse> create(
            @Valid @RequestBody CreateTicketRequest request,
            Authentication authentication
    ) {
        Ticket ticket = createTicket.create(request.toCommand(authentication.getName()));
        return ResponseEntity
                .created(URI.create("/api/tickets/" + ticket.getId()))
                .body(TicketResponse.from(ticket));
    }

    // KR-301: handläggare (och administratörer) ser den samlade ärendekön.
    @GetMapping
    @PreAuthorize("hasAnyRole('Agent', 'Admin')")
    public List<TicketResponse> list() {
        return listTickets.listAll().stream().map(TicketResponse::from).toList();
    }

    // Handläggare öppnar ett enskilt ärende för att se full information (KR-301).
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Agent', 'Admin')")
    public TicketResponse get(@PathVariable Long id) {
        return TicketResponse.from(getTicket.getById(id));
    }

    // Handläggare ändrar status/prioritet på ett ärende (KR-203/KR-301).
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('Agent', 'Admin')")
    public TicketResponse update(@PathVariable Long id, @Valid @RequestBody UpdateTicketRequest request) {
        return TicketResponse.from(updateTicket.update(request.toCommand(id)));
    }

    // Lokal hanterare (inte den delade ApiExceptionHandler) — TicketNotFoundException
    // är specifik för den här bounded contexten.
    @ExceptionHandler(TicketNotFoundException.class)
    public ProblemDetail handleNotFound(TicketNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Ticket not found");
        problem.setDetail("No ticket exists with the given id.");
        return problem;
    }
}
