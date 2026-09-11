package se.foi.xelin.ticket.domain.model;

// Ärendets livscykelstatus (KR-203). Ett nytt ärende börjar som NEW.
public enum TicketStatus {
    NEW,
    ASSIGNED,
    IN_PROGRESS,
    PENDING,
    RESOLVED,
    CLOSED
}
