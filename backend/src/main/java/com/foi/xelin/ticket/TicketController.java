// LAGER: Controller – tar emot HTTP-anrop och skickar svar.
// Ansvarar INTE för logik; delegerar direkt till servicelagret.
// Känner till HTTP (status, JSON, validering) men inte databasen.
package com.foi.xelin.ticket;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
class TicketController {

    private final TicketService ticketService;

    TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // GET /api/tickets – hämta alla ärenden
    @GetMapping
    List<TicketResponse> getAll() {
        return ticketService.getAll();
    }

    // POST /api/tickets – användaren postar formuläret från UI
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    TicketResponse create(@Valid @RequestBody CreateTicketRequest request) {
        return ticketService.create(request);
    }
}
