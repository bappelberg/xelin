package se.foi.xelin.ticket.application.port.in;

import se.foi.xelin.ticket.domain.model.TicketComment;

import java.util.List;

// Vad kan systemet göra? Lista kommentarerna på ett ärende (KR-208). Interna kommentarer
// filtreras bort om anroparen inte är handläggare eller administratör.
// Kastar TicketNotFoundException om ärendet saknas, TicketAccessDeniedException om en
// slutanvändare försöker se ett ärende som inte är deras eget.
public interface ListTicketCommentsUseCase {
    List<TicketComment> listComments(ListTicketCommentsQuery query);
}
