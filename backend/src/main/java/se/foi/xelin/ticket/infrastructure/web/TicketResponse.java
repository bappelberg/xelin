package se.foi.xelin.ticket.infrastructure.web;

import se.foi.xelin.ticket.domain.model.Ticket;

import java.time.Instant;

// Utgående JSON efter att ett ärende skapats. Innehåller det tilldelade ärende-ID:t (KR-202).
public class TicketResponse {

    private final Long id;
    private final String status;
    private final String priority;
    private final String category;
    private final Instant createdAt;

    private TicketResponse(Long id, String status, String priority, String category, Instant createdAt) {
        this.id = id;
        this.status = status;
        this.priority = priority;
        this.category = category;
        this.createdAt = createdAt;
    }

    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getStatus().name(),
                ticket.getPriority().name(),
                ticket.getCategory().name(),
                ticket.getCreatedAt());
    }

    public Long getId() {
        return id;
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

    public Instant getCreatedAt() {
        return createdAt;
    }
}
