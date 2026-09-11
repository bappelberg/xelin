package se.foi.xelin.ticket.domain.model;

// Kastas när ett ärende-ID inte finns. Mappas till 404 i TicketController.
public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(Long ticketId) {
        super("Ticket not found: " + ticketId);
    }
}
