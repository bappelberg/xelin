package se.foi.xelin.ticket.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

// Spring Data-repository för JPA-entiteten. Används bara av persistence-adaptern.
public interface TicketJpaRepository extends JpaRepository<TicketJpaEntity, Long> {
}
