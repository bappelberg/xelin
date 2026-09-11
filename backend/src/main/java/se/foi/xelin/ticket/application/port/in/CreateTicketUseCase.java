package se.foi.xelin.ticket.application.port.in;

import se.foi.xelin.ticket.domain.model.Ticket;

// Vad kan systemet göra? Slutanvändare registrerar ett ärende (KR-201).
public interface CreateTicketUseCase {
    Ticket create(CreateTicketCommand command);
}
