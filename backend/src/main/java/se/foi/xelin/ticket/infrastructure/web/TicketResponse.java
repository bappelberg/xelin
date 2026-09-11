package se.foi.xelin.ticket.infrastructure.web;

import se.foi.xelin.ticket.domain.model.Ticket;

import java.time.Instant;

// Utgående JSON efter att ett ärende skapats. Innehåller det tilldelade ärende-ID:t (KR-202).
public class TicketResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final String status;
    private final String priority;
    private final String category;
    private final String reporter;
    private final Instant createdAt;

    private TicketResponse(Long id, String title, String description, String status, String priority,
                           String category, String reporter, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.category = category;
        this.reporter = reporter;
        this.createdAt = createdAt;
    }

    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus().name(),
                ticket.getPriority().name(),
                ticket.getCategory().name(),
                ticket.getReporter(),
                ticket.getCreatedAt());
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public String getCategory() {
        return category;
    }

    public String getReporter() {
        return reporter;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
