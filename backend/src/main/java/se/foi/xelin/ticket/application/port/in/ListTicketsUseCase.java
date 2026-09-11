package se.foi.xelin.ticket.application.port.in;

import se.foi.xelin.ticket.domain.model.Ticket;

import java.util.List;

// Vad kan systemet göra? Handläggare ser den samlade ärendekön (KR-301).
public interface ListTicketsUseCase {
    List<Ticket> listAll();
}
