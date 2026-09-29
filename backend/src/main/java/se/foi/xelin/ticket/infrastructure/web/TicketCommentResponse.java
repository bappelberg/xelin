package se.foi.xelin.ticket.infrastructure.web;

import se.foi.xelin.ticket.domain.model.TicketComment;

import java.time.Instant;

// Utgående JSON för en ärendekommentar (KR-208).
public class TicketCommentResponse {

    private final Long id;
    private final Long ticketId;
    private final String author;
    private final String body;
    private final boolean internal;
    private final Instant createdAt;

    private TicketCommentResponse(Long id, Long ticketId, String author, String body,
                                  boolean internal, Instant createdAt) {
        this.id = id;
        this.ticketId = ticketId;
        this.author = author;
        this.body = body;
        this.internal = internal;
        this.createdAt = createdAt;
    }

    public static TicketCommentResponse from(TicketComment comment) {
        return new TicketCommentResponse(
                comment.getId(),
                comment.getTicketId(),
                comment.getAuthor(),
                comment.getBody(),
                comment.isInternal(),
                comment.getCreatedAt());
    }

    public Long getId() {
        return id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public String getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public boolean isInternal() {
        return internal;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
