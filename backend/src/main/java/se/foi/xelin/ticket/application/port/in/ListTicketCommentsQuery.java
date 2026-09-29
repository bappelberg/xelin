package se.foi.xelin.ticket.application.port.in;

// Indata till use caset: någon listar kommentarerna på ett ärende (KR-208).
// requester/requesterIsAgentOrAdmin avgör ägarkontroll och om interna kommentarer inkluderas.
public record ListTicketCommentsQuery(
        Long ticketId,
        String requester,
        boolean requesterIsAgentOrAdmin
) {
}
