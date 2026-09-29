package se.foi.xelin.ticket.application.port.in;

// Indata till use caset: någon lägger till en kommentar på ett ärende (KR-208).
// author härleds från den inloggade sessionen, aldrig från klientens begäran (KR-804).
// requesterIsAgentOrAdmin avgör om internal-flaggan respekteras och om ägarkontrollen kringgås —
// beräknas server-side av controllern utifrån den inloggades roller, inte av klienten.
public record AddTicketCommentCommand(
        Long ticketId,
        String author,
        String body,
        boolean internal,
        boolean requesterIsAgentOrAdmin
) {
}
