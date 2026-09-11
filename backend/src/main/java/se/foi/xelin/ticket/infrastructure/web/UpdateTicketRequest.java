package se.foi.xelin.ticket.infrastructure.web;

import jakarta.validation.constraints.NotNull;
import se.foi.xelin.ticket.application.port.in.UpdateTicketCommand;
import se.foi.xelin.ticket.domain.model.TicketCategory;
import se.foi.xelin.ticket.domain.model.TicketPriority;
import se.foi.xelin.ticket.domain.model.TicketStatus;

// Inkommande JSON för att ändra status/prioritet/kategori på ett ärende. Aldrig domän- eller JPA-objekt över tråden.
public class UpdateTicketRequest {

    @NotNull
    private TicketStatus status;

    @NotNull
    private TicketPriority priority;

    @NotNull
    private TicketCategory category;

    public UpdateTicketRequest() {
        // Krävs av Jackson.
    }

    public UpdateTicketCommand toCommand(Long ticketId) {
        return new UpdateTicketCommand(ticketId, status, priority, category);
    }

    // Setters används av Jackson vid JSON-inläsning. Getters utelämnas — DTO:n läses aldrig ut.
    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    public void setCategory(TicketCategory category) {
        this.category = category;
    }
}
