package se.foi.xelin.ticket.infrastructure.web;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.foi.xelin.ticket.application.port.in.CreateTicketUseCase;
import se.foi.xelin.ticket.domain.model.Ticket;

import java.net.URI;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final CreateTicketUseCase createTicket;

    public TicketController(CreateTicketUseCase createTicket) {
        this.createTicket = createTicket;
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
}
