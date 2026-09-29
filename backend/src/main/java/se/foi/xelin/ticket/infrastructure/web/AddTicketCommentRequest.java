package se.foi.xelin.ticket.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import se.foi.xelin.ticket.application.port.in.AddTicketCommentCommand;

// Inkommande JSON för att lägga till en kommentar på ett ärende (KR-208).
// Aldrig domän- eller JPA-objekt över tråden.
public class AddTicketCommentRequest {

    @NotBlank
    @Size(max = 4000)
    private String body;

    // Slutanvändare kan inte sätta denna till true — TicketCommentService tvingar den
    // till false om anroparen inte är handläggare eller administratör.
    private boolean internal;

    public AddTicketCommentRequest() {
        // Krävs av Jackson.
    }

    // author och requesterIsAgentOrAdmin härleds från den inloggade sessionen server-side,
    // aldrig från klientens begäran (KR-804).
    public AddTicketCommentCommand toCommand(Long ticketId, String author, boolean requesterIsAgentOrAdmin) {
        return new AddTicketCommentCommand(ticketId, author, body, internal, requesterIsAgentOrAdmin);
    }

    // Setters används av Jackson vid JSON-inläsning. Getters utelämnas — DTO:n läses aldrig ut.
    public void setBody(String body) {
        this.body = body;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }
}
