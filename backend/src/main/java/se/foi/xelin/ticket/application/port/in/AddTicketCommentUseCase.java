package se.foi.xelin.ticket.application.port.in;

import se.foi.xelin.ticket.domain.model.TicketComment;

// Vad kan systemet göra? Handläggare och slutanvändare kommenterar ett ärende (KR-208).
// Kastar TicketNotFoundException om ärendet saknas, TicketAccessDeniedException om en
// slutanvändare försöker kommentera någon annans ärende.
public interface AddTicketCommentUseCase {
    TicketComment addComment(AddTicketCommentCommand command);
}
