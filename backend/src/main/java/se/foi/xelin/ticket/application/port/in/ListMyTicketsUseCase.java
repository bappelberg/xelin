package se.foi.xelin.ticket.application.port.in;

import se.foi.xelin.ticket.domain.model.Ticket;

import java.util.List;

// Vad kan systemet göra? Slutanvändaren ser sina egna ärenden (KR-207).
public interface ListMyTicketsUseCase {
    List<Ticket> listByReporter(String reporter);
}
