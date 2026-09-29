package se.foi.xelin.ticket.domain.model;

import java.time.Instant;
import java.util.Objects;

// En kommentar på ett ärende (KR-208). Interna kommentarer är bara synliga för
// handläggare och administratörer — slutanvändare kan aldrig skapa dem.
public class TicketComment {

    private final Long id;                // null tills kommentaren har persisterats
    private final Long ticketId;
    private final String author;          // uid för den som skrev kommentaren
    private final String body;
    private final boolean internal;
    private final Instant createdAt;

    // Skapar en ny kommentar med skapandetidpunkt satt direkt.
    public static TicketComment create(Long ticketId, String author, String body, boolean internal) {
        return new TicketComment(null, ticketId, author, body, internal, Instant.now());
    }

    // Återskapar en kommentar från lagringen.
    public TicketComment(Long id, Long ticketId, String author, String body, boolean internal, Instant createdAt) {
        this.ticketId = Objects.requireNonNull(ticketId, "ticketId");
        this.author = requireText(author, "author");
        this.body = requireText(body, "body");
        this.internal = internal;
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
