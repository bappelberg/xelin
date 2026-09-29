package se.foi.xelin.ticket.infrastructure.persistence;

import org.springframework.stereotype.Component;
import se.foi.xelin.ticket.application.port.out.TicketCommentRepository;
import se.foi.xelin.ticket.domain.model.TicketComment;

import java.util.List;

// Implementerar port/out mot Spring Data. Mappar mellan domänmodell och JPA-entitet.
@Component
public class TicketCommentPersistenceAdapter implements TicketCommentRepository {

    private final TicketCommentJpaRepository jpaRepository;

    public TicketCommentPersistenceAdapter(TicketCommentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TicketComment save(TicketComment comment) {
        TicketCommentJpaEntity saved = jpaRepository.save(toEntity(comment));
        return toDomain(saved);
    }

    @Override
    public List<TicketComment> findByTicketId(Long ticketId) {
        return jpaRepository.findByTicketIdOrderByCreatedAtDesc(ticketId).stream().map(this::toDomain).toList();
    }

    private TicketCommentJpaEntity toEntity(TicketComment comment) {
        return new TicketCommentJpaEntity(
                comment.getId(),
                comment.getTicketId(),
                comment.getAuthor(),
                comment.getBody(),
                comment.isInternal(),
                comment.getCreatedAt());
    }

    private TicketComment toDomain(TicketCommentJpaEntity entity) {
        return new TicketComment(
                entity.getId(),
                entity.getTicketId(),
                entity.getAuthor(),
                entity.getBody(),
                entity.isInternal(),
                entity.getCreatedAt());
    }
}
