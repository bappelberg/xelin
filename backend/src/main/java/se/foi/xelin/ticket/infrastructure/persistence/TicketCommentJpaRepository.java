package se.foi.xelin.ticket.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Spring Data-repository för JPA-entiteten. Används bara av persistence-adaptern.
public interface TicketCommentJpaRepository extends JpaRepository<TicketCommentJpaEntity, Long> {
    List<TicketCommentJpaEntity> findByTicketIdOrderByCreatedAtDesc(Long ticketId);
}
