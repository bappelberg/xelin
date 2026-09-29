package se.foi.xelin.ticket.application.port.out;

import se.foi.xelin.ticket.domain.model.TicketComment;

import java.util.List;

// Vad behöver systemet från omvärlden? Beständig lagring av ärendekommentarer.
public interface TicketCommentRepository {
    TicketComment save(TicketComment comment);

    List<TicketComment> findByTicketId(Long ticketId);
}
