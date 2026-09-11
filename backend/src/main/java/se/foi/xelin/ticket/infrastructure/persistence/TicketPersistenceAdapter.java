package se.foi.xelin.ticket.infrastructure.persistence;

import org.springframework.stereotype.Component;
import se.foi.xelin.ticket.application.port.out.TicketRepository;
import se.foi.xelin.ticket.domain.model.Ticket;

// Implementerar port/out mot Spring Data. Mappar mellan domänmodell och JPA-entitet.
@Component
public class TicketPersistenceAdapter implements TicketRepository {

    private final TicketJpaRepository jpaRepository;

    public TicketPersistenceAdapter(TicketJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Ticket save(Ticket ticket) {
        TicketJpaEntity saved = jpaRepository.save(toEntity(ticket));
        return toDomain(saved);
    }

    private TicketJpaEntity toEntity(Ticket ticket) {
        return new TicketJpaEntity(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority(),
                ticket.getCategory(),
                ticket.getStatus(),
                ticket.getReporter(),
                ticket.getCreatedAt());
    }

    private Ticket toDomain(TicketJpaEntity entity) {
        return new Ticket(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getPriority(),
                entity.getCategory(),
                entity.getStatus(),
                entity.getReporter(),
                entity.getCreatedAt());
    }
}
