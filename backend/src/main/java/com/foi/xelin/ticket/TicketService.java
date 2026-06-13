// LAGER: Service – all affärslogik bor här.
// Tar emot DTO från controllern, arbetar med entiteter, sparar via repository.
// Varken HTTP-detaljer (request/response) eller SQL-detaljer ska finnas här.
package com.foi.xelin.ticket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
class TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;

    TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional(readOnly = true)
    List<TicketResponse> getAll() {
        return ticketRepository.findAll().stream()
                .map(TicketResponse::from)
                .toList();
    }

    @Transactional
    TicketResponse create(CreateTicketRequest request) {
        Ticket ticket = new Ticket(request.title(), request.description());
        Ticket saved = ticketRepository.save(ticket);
        log.info("Ärende skapat id={} title='{}'", saved.getId(), saved.getTitle());
        return TicketResponse.from(saved);
    }
}
