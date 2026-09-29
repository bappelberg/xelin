package se.foi.xelin.ticket.infrastructure.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.foi.xelin.ticket.application.port.in.AddTicketCommentUseCase;
import se.foi.xelin.ticket.application.port.in.ListTicketCommentsQuery;
import se.foi.xelin.ticket.application.port.in.ListTicketCommentsUseCase;
import se.foi.xelin.ticket.domain.model.TicketAccessDeniedException;
import se.foi.xelin.ticket.domain.model.TicketComment;
import se.foi.xelin.ticket.domain.model.TicketNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
public class TicketCommentController {

    private final AddTicketCommentUseCase addComment;
    private final ListTicketCommentsUseCase listComments;

    public TicketCommentController(AddTicketCommentUseCase addComment, ListTicketCommentsUseCase listComments) {
        this.addComment = addComment;
        this.listComments = listComments;
    }

    // KR-208: handläggare och slutanvändare kan lägga till kommentarer på ett ärende.
    // Slutanvändare får bara kommentera egna ärenden och kan aldrig skapa interna
    // kommentarer — kontrolleras server-side i TicketCommentService (KR-804).
    @PostMapping
    @PreAuthorize("hasAnyRole('User', 'Agent', 'Admin')")
    public ResponseEntity<TicketCommentResponse> add(
            @PathVariable Long ticketId,
            @Valid @RequestBody AddTicketCommentRequest request,
            Authentication authentication
    ) {
        TicketComment comment = addComment.addComment(
                request.toCommand(ticketId, authentication.getName(), isAgentOrAdmin(authentication)));
        return ResponseEntity.status(HttpStatus.CREATED).body(TicketCommentResponse.from(comment));
    }

    // KR-208: interna kommentarer är bara synliga för handläggare och administratörer.
    @GetMapping
    @PreAuthorize("hasAnyRole('User', 'Agent', 'Admin')")
    public List<TicketCommentResponse> list(@PathVariable Long ticketId, Authentication authentication) {
        ListTicketCommentsQuery query = new ListTicketCommentsQuery(
                ticketId, authentication.getName(), isAgentOrAdmin(authentication));
        return listComments.listComments(query).stream().map(TicketCommentResponse::from).toList();
    }

    private boolean isAgentOrAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_Agent")
                        || authority.getAuthority().equals("ROLE_Admin"));
    }

    // Lokala hanterare (inte den delade ApiExceptionHandler) — dessa fel är specifika
    // för ärendekontexten.
    @ExceptionHandler(TicketNotFoundException.class)
    public ProblemDetail handleNotFound(TicketNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Ticket not found");
        problem.setDetail("No ticket exists with the given id.");
        return problem;
    }

    @ExceptionHandler(TicketAccessDeniedException.class)
    public ProblemDetail handleAccessDenied(TicketAccessDeniedException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setTitle("Access denied");
        problem.setDetail("You do not have permission to access this ticket.");
        return problem;
    }
}
