package se.foi.xelin.ticket.application.port.in;

import se.foi.xelin.ticket.domain.model.TicketCategory;
import se.foi.xelin.ticket.domain.model.TicketPriority;

// Indata till use caset. reporter härleds från den inloggade sessionen server-side,
// aldrig från klientens begäran (KR-804).
public record CreateTicketCommand(
        String reporter,
        String title,
        String description,
        TicketPriority priority,
        TicketCategory category
) {
}
