package se.foi.xelin.ticket.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import se.foi.xelin.ticket.domain.model.TicketCategory;
import se.foi.xelin.ticket.domain.model.TicketPriority;
import se.foi.xelin.ticket.domain.model.TicketStatus;

import java.time.Instant;

// JPA-representation av ett ärende. Skild från domänmodellen; adaptern mappar mellan dem.
@Entity
@Table(name = "ticket")
public class TicketJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus status;

    @Column(nullable = false, length = 100)
    private String reporter;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected TicketJpaEntity() {
        // Krävs av JPA.
    }

    public TicketJpaEntity(Long id, String title, String description, TicketPriority priority,
                           TicketCategory category, TicketStatus status, String reporter,
                           Instant createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.category = category;
        this.status = status;
        this.reporter = reporter;
        this.createdAt = createdAt;
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

    public TicketPriority getPriority() {
        return priority;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public String getReporter() {
        return reporter;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
