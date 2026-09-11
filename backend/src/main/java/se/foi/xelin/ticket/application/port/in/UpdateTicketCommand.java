package se.foi.xelin.ticket.application.port.in;

import se.foi.xelin.ticket.domain.model.TicketCategory;
import se.foi.xelin.ticket.domain.model.TicketPriority;
import se.foi.xelin.ticket.domain.model.TicketStatus;

// Indata till use caset: handläggaren ändrar status, prioritet och/eller kategori på ett ärende.
public record UpdateTicketCommand(
        Long ticketId,
        TicketStatus status,
        TicketPriority priority,
        TicketCategory category
) {
}
