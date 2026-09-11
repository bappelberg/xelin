package se.foi.xelin.ticket.application.port.in;

import se.foi.xelin.ticket.domain.model.Ticket;

// Vad kan systemet göra? Handläggare ändrar status/prioritet på ett ärende (KR-203/KR-301).
public interface UpdateTicketUseCase {
    Ticket update(UpdateTicketCommand command);
}
