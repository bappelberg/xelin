package se.foi.xelin.ticket.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import se.foi.xelin.ticket.application.port.in.CreateTicketCommand;
import se.foi.xelin.ticket.domain.model.TicketCategory;
import se.foi.xelin.ticket.domain.model.TicketPriority;

// Inkommande JSON för att skapa ett ärende (KR-201). Aldrig domän- eller JPA-objekt över tråden.
public class CreateTicketRequest {

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private TicketPriority priority;

    @NotNull
    private TicketCategory category;

    public CreateTicketRequest() {
        // Krävs av Jackson.
    }

    // reporter kommer från den autentiserade sessionen, aldrig från klienten (KR-804).
    public CreateTicketCommand toCommand(String reporter) {
        return new CreateTicketCommand(reporter, title, description, priority, category);
    }

    // Setters används av Jackson vid JSON-inläsning. Getters utelämnas — DTO:n läses aldrig ut.
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    public void setCategory(TicketCategory category) {
        this.category = category;
    }
}
