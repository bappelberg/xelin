package se.foi.xelin.ticket.application.port.in;

import se.foi.xelin.ticket.domain.model.Ticket;

// Systemets förmåga: tilldela ett ärende till en handläggare (KR-204).
public interface AssignTicketUseCase {
    Ticket assign(AssignTicketCommand command);
}
