package se.foi.xelin.ticket.domain.model;

// Kastas när en slutanvändare försöker komma åt eller kommentera ett ärende som
// inte är deras eget (KR-208/KR-804). Mappas till 403 i TicketCommentController.
public class TicketAccessDeniedException extends RuntimeException {

    public TicketAccessDeniedException(Long ticketId) {
        super("Access denied to ticket: " + ticketId);
    }
}
