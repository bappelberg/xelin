// LAGER: DTO (Data Transfer Object) – vad vi skickar tillbaka till klienten.
// Exponerar aldrig entiteten direkt; kontrollerar exakt vad frontend ser.
package com.foi.xelin.ticket;

import java.time.Instant;

record TicketResponse(
        Long id,
        String title,
        String description,
        String status,
        Instant createdAt
) {
    static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus().name(),
                ticket.getCreatedAt()
        );
    }
}
