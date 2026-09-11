package se.foi.xelin.ticket.application.port.in;

import se.foi.xelin.ticket.domain.model.Ticket;

// Vad kan systemet göra? Handläggare öppnar ett enskilt ärende (KR-301).
// Kastar TicketNotFoundException om id:t inte finns.
public interface GetTicketUseCase {
    Ticket getById(Long id);
}
