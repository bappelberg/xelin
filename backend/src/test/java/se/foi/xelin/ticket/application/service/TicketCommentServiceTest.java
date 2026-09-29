package se.foi.xelin.ticket.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.foi.xelin.ticket.application.port.in.AddTicketCommentCommand;
import se.foi.xelin.ticket.application.port.in.ListTicketCommentsQuery;
import se.foi.xelin.ticket.application.port.out.TicketCommentRepository;
import se.foi.xelin.ticket.application.port.out.TicketRepository;
import se.foi.xelin.ticket.domain.model.Ticket;
import se.foi.xelin.ticket.domain.model.TicketAccessDeniedException;
import se.foi.xelin.ticket.domain.model.TicketCategory;
import se.foi.xelin.ticket.domain.model.TicketComment;
import se.foi.xelin.ticket.domain.model.TicketNotFoundException;
import se.foi.xelin.ticket.domain.model.TicketPriority;
import se.foi.xelin.ticket.domain.model.TicketStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketCommentServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketCommentRepository commentRepository;

    @InjectMocks
    private TicketCommentService commentService;

    private Ticket ticketReportedByBen() {
        return new Ticket(1L, "Skrivaren fungerar inte", "Felkod E-52",
                TicketPriority.NORMAL, TicketCategory.HARDWARE, TicketStatus.NEW,
                "ben", Instant.now());
    }

    @Test
    void slutanvandare_kan_kommentera_eget_arende() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketReportedByBen()));
        when(commentRepository.save(any(TicketComment.class))).thenAnswer(i -> i.getArgument(0));

        TicketComment result = commentService.addComment(
                new AddTicketCommentCommand(1L, "ben", "Fortfarande trasig", false, false));

        assertThat(result.getAuthor()).isEqualTo("ben");
        assertThat(result.isInternal()).isFalse();
    }

    @Test
    void slutanvandare_far_inte_kommentera_nagon_annans_arende() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketReportedByBen()));

        assertThatThrownBy(() -> commentService.addComment(
                new AddTicketCommentCommand(1L, "bob", "Jag kikar in", false, false)))
                .isInstanceOf(TicketAccessDeniedException.class);
    }

    @Test
    void slutanvandare_kan_inte_skapa_intern_kommentar_aven_om_flaggan_satts() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketReportedByBen()));
        ArgumentCaptor<TicketComment> captor = ArgumentCaptor.forClass(TicketComment.class);
        when(commentRepository.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        commentService.addComment(new AddTicketCommentCommand(1L, "ben", "Test", true, false));

        assertThat(captor.getValue().isInternal()).isFalse();
    }

    @Test
    void handlaggare_kan_skapa_intern_kommentar() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketReportedByBen()));
        when(commentRepository.save(any(TicketComment.class))).thenAnswer(i -> i.getArgument(0));

        TicketComment result = commentService.addComment(
                new AddTicketCommentCommand(1L, "agnes", "Internt: väntar på reservdel", true, true));

        assertThat(result.isInternal()).isTrue();
        assertThat(result.getAuthor()).isEqualTo("agnes");
    }

    @Test
    void kommentar_pa_okant_arende_kastar_TicketNotFoundException() {
        when(ticketRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.addComment(
                new AddTicketCommentCommand(9999L, "ben", "Test", false, false)))
                .isInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void handlaggare_ser_bade_publika_och_interna_kommentarer() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketReportedByBen()));
        when(commentRepository.findByTicketId(1L)).thenReturn(List.of(
                new TicketComment(1L, 1L, "ben", "Publik kommentar", false, Instant.now()),
                new TicketComment(2L, 1L, "agnes", "Intern kommentar", true, Instant.now())));

        List<TicketComment> result = commentService.listComments(
                new ListTicketCommentsQuery(1L, "agnes", true));

        assertThat(result).hasSize(2);
    }

    @Test
    void slutanvandare_ser_bara_publika_kommentarer_pa_eget_arende() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketReportedByBen()));
        when(commentRepository.findByTicketId(1L)).thenReturn(List.of(
                new TicketComment(1L, 1L, "ben", "Publik kommentar", false, Instant.now()),
                new TicketComment(2L, 1L, "agnes", "Intern kommentar", true, Instant.now())));

        List<TicketComment> result = commentService.listComments(
                new ListTicketCommentsQuery(1L, "ben", false));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isInternal()).isFalse();
    }

    @Test
    void slutanvandare_far_inte_lista_kommentarer_pa_nagon_annans_arende() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketReportedByBen()));

        assertThatThrownBy(() -> commentService.listComments(
                new ListTicketCommentsQuery(1L, "bob", false)))
                .isInstanceOf(TicketAccessDeniedException.class);

        verify(commentRepository, org.mockito.Mockito.never()).findByTicketId(any());
    }
}
