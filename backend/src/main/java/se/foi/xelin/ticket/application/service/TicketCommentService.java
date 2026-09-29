package se.foi.xelin.ticket.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.foi.xelin.ticket.application.port.in.AddTicketCommentCommand;
import se.foi.xelin.ticket.application.port.in.AddTicketCommentUseCase;
import se.foi.xelin.ticket.application.port.in.ListTicketCommentsQuery;
import se.foi.xelin.ticket.application.port.in.ListTicketCommentsUseCase;
import se.foi.xelin.ticket.application.port.out.TicketCommentRepository;
import se.foi.xelin.ticket.application.port.out.TicketRepository;
import se.foi.xelin.ticket.domain.model.Ticket;
import se.foi.xelin.ticket.domain.model.TicketAccessDeniedException;
import se.foi.xelin.ticket.domain.model.TicketComment;
import se.foi.xelin.ticket.domain.model.TicketNotFoundException;

import java.util.List;

@Service
public class TicketCommentService implements AddTicketCommentUseCase, ListTicketCommentsUseCase {

    private static final Logger log = LoggerFactory.getLogger(TicketCommentService.class);

    private final TicketRepository ticketRepository;
    private final TicketCommentRepository commentRepository;

    public TicketCommentService(TicketRepository ticketRepository, TicketCommentRepository commentRepository) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public TicketComment addComment(AddTicketCommentCommand command) {
        Ticket ticket = findTicketOrThrow(command.ticketId());
        requireOwnershipUnlessPrivileged(ticket, command.author(), command.requesterIsAgentOrAdmin());

        // Bara handläggare/administratörer får markera en kommentar som intern (KR-208).
        boolean internal = command.requesterIsAgentOrAdmin() && command.internal();

        TicketComment saved = commentRepository.save(
                TicketComment.create(command.ticketId(), command.author(), command.body(), internal));

        log.info("Kommentar tillagd: ticketId={} author={} internal={}",
                saved.getTicketId(), saved.getAuthor(), saved.isInternal());

        return saved;
    }

    @Override
    public List<TicketComment> listComments(ListTicketCommentsQuery query) {
        Ticket ticket = findTicketOrThrow(query.ticketId());
        requireOwnershipUnlessPrivileged(ticket, query.requester(), query.requesterIsAgentOrAdmin());

        List<TicketComment> comments = commentRepository.findByTicketId(query.ticketId());
        if (query.requesterIsAgentOrAdmin()) {
            return comments;
        }

        // Slutanvändare ser aldrig interna kommentarer (KR-208).
        return comments.stream().filter(comment -> !comment.isInternal()).toList();
    }

    private Ticket findTicketOrThrow(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));
    }

    private void requireOwnershipUnlessPrivileged(Ticket ticket, String requester, boolean privileged) {
        if (!privileged && !ticket.getReporter().equals(requester)) {
            throw new TicketAccessDeniedException(ticket.getId());
        }
    }
}
