package se.foi.xelin.ticket.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

// JPA-representation av en ärendekommentar. Skild från domänmodellen; adaptern mappar mellan dem.
@Entity
@Table(name = "ticket_comment")
public class TicketCommentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private boolean internal;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected TicketCommentJpaEntity() {
        // Krävs av JPA.
    }

    public TicketCommentJpaEntity(Long id, Long ticketId, String author, String body,
                                  boolean internal, Instant createdAt) {
        this.id = id;
        this.ticketId = ticketId;
        this.author = author;
        this.body = body;
        this.internal = internal;
        this.createdAt = createdAt;
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
