package se.foi.xelin.ticket.application.port.out;

import se.foi.xelin.ticket.domain.model.Ticket;

// Vad behöver systemet från omvärlden? Beständig lagring av ärenden.
public interface TicketRepository {
    Ticket save(Ticket ticket);
}
