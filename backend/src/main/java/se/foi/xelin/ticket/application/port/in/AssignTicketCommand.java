package se.foi.xelin.ticket.application.port.in;

// Indata till use caset: en handläggare tilldelas ett ärende (KR-204).
// assignee är uid för handläggaren som ska ta ärendet — sig själv eller en kollega.
public record AssignTicketCommand(
        Long ticketId,
        String assignee
) {
}
