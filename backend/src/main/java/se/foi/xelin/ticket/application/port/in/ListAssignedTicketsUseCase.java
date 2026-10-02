package se.foi.xelin.ticket.application.port.in;

import se.foi.xelin.ticket.domain.model.Ticket;

import java.util.List;

// Vad kan systemet göra? Handläggaren ser sin egen tilldelade ärendekö, inga andras ärenden.
public interface ListAssignedTicketsUseCase {
    List<Ticket> listByAssignee(String assignee);
}
