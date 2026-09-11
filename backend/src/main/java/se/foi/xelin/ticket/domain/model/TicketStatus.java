package se.foi.xelin.ticket.domain.model;

// Ärendets livscykelstatus (KR-203). Ett nytt ärende börjar som NY.
public enum TicketStatus {
    NY,
    TILLDELAD,
    PAGAENDE,
    VANTANDE,
    LOST,
    STANGD
}
