package se.foi.xelin.ticket.domain.model;

import java.time.Instant;
import java.util.Objects;

// Ett registrerat IT-supportärende (KR-201). Ren domänmodell — ingen Spring, ingen JPA.
public class Ticket {

    private final Long id;               // null tills ärendet har persisterats
    private final String title;
    private final String description;
    private final TicketPriority priority;
    private final TicketCategory category;
    private final TicketStatus status;
    private final String reporter;       // uid för slutanvändaren som skapade ärendet
    private final Instant createdAt;

    // Skapar ett nytt ärende: status NEW (KR-203) och skapandetidpunkt sätts direkt.
    public static Ticket create(String title, String description, TicketPriority priority,
                                TicketCategory category, String reporter) {
        return new Ticket(null, title, description, priority, category,
                TicketStatus.NEW, reporter, Instant.now());
    }

    // Återskapar ett ärende från lagringen.
    public Ticket(Long id, String title, String description, TicketPriority priority,
                  TicketCategory category, TicketStatus status, String reporter, Instant createdAt) {
        this.title = requireText(title, "title");
        this.description = requireText(description, "description");
        this.priority = Objects.requireNonNull(priority, "priority");
        this.category = Objects.requireNonNull(category, "category");
        this.status = Objects.requireNonNull(status, "status");
        this.reporter = requireText(reporter, "reporter");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.id = id;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " får inte vara tomt");
        }
        return value;
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

    // Handläggare ändrar status, prioritet och kategori (KR-203/KR-301). Övriga fält är
    // oföränderliga efter registrering — ny instans, samma id/skapandetidpunkt.
    public Ticket update(TicketStatus newStatus, TicketPriority newPriority, TicketCategory newCategory) {
        return new Ticket(id, title, description, newPriority, newCategory, newStatus, reporter, createdAt);
    }
}
