// LAGER: Domain – JPA-entitet som speglar tickets-tabellen i databasen.
// Innehåller inga affärsregler, bara data och mappning.
package com.foi.xelin.ticket;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "tickets")
class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Ticket() {}

    Ticket(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TicketStatus.NY;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    Long getId()              { return id; }
    String getTitle()         { return title; }
    String getDescription()   { return description; }
    TicketStatus getStatus()  { return status; }
    Instant getCreatedAt()    { return createdAt; }
    Instant getUpdatedAt()    { return updatedAt; }
}
