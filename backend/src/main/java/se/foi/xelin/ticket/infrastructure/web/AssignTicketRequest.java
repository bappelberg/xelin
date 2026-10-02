package se.foi.xelin.ticket.infrastructure.web;

import se.foi.xelin.ticket.application.port.in.AssignTicketCommand;

// Inkommande JSON för att tilldela ett ärende (KR-204). assignee utelämnad eller null
// betyder att den inloggade handläggaren tilldelar sig ärendet själv.
public class AssignTicketRequest {

    private String assignee;

    public AssignTicketRequest() {
        // Krävs av Jackson.
    }

    public AssignTicketCommand toCommand(Long ticketId, String currentUser) {
        String target = (assignee == null || assignee.isBlank()) ? currentUser : assignee;
        return new AssignTicketCommand(ticketId, target);
    }

    // Setter används av Jackson vid JSON-inläsning. Getter utelämnas — DTO:n läses aldrig ut.
    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
}
