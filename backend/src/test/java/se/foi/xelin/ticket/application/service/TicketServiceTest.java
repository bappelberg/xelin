package se.foi.xelin.ticket.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.foi.xelin.ticket.application.port.in.CreateTicketCommand;
import se.foi.xelin.ticket.application.port.in.UpdateTicketCommand;
import se.foi.xelin.ticket.application.port.out.TicketRepository;
import se.foi.xelin.ticket.domain.model.Ticket;
import se.foi.xelin.ticket.domain.model.TicketCategory;
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
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void nytt_arende_far_status_NEW_och_reporter_fran_kommandot() {
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket t = invocation.getArgument(0);
            return new Ticket(42L, t.getTitle(), t.getDescription(), t.getPriority(),
                    t.getCategory(), t.getStatus(), t.getReporter(), t.getCreatedAt());
        });

        CreateTicketCommand command = new CreateTicketCommand(
                "ben", "Skrivaren fungerar inte", "Får felkod E-52",
                TicketPriority.NORMAL, TicketCategory.HARDWARE);

        Ticket result = ticketService.create(command);

        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getStatus()).isEqualTo(TicketStatus.NEW);
        assertThat(result.getReporter()).isEqualTo("ben");
    }

    @Test
    void arendet_sparas_utan_id_och_med_skapandetidpunkt() {
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        ticketService.create(new CreateTicketCommand(
                "bob", "Kan inte logga in", "Kontot verkar låst",
                TicketPriority.HIGH, TicketCategory.ACCOUNT));

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(captor.capture());

        Ticket persisted = captor.getValue();
        assertThat(persisted.getId()).isNull();
        assertThat(persisted.getStatus()).isEqualTo(TicketStatus.NEW);
        assertThat(persisted.getCreatedAt()).isNotNull();
    }

    @Test
    void listAll_delegerar_till_repositoryt() {
        Ticket t = new Ticket(1L, "Skrivaren fungerar inte", "Felkod E-52",
                TicketPriority.NORMAL, TicketCategory.HARDWARE, TicketStatus.NEW,
                "ben", Instant.now());
        when(ticketRepository.findAll()).thenReturn(List.of(t));

        List<Ticket> result = ticketService.listAll();

        assertThat(result).containsExactly(t);
    }

    @Test
    void getById_kastar_TicketNotFoundException_om_arendet_saknas() {
        when(ticketRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.getById(9999L))
                .isInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void update_andrar_status_och_prioritet_och_sparar() {
        Ticket existing = new Ticket(1L, "Skrivaren fungerar inte", "Felkod E-52",
                TicketPriority.NORMAL, TicketCategory.HARDWARE, TicketStatus.NEW,
                "ben", Instant.now());
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        Ticket result = ticketService.update(
                new UpdateTicketCommand(1L, TicketStatus.ASSIGNED, TicketPriority.HIGH, TicketCategory.NETWORK));

        assertThat(result.getStatus()).isEqualTo(TicketStatus.ASSIGNED);
        assertThat(result.getPriority()).isEqualTo(TicketPriority.HIGH);
        assertThat(result.getCategory()).isEqualTo(TicketCategory.NETWORK);
        assertThat(result.getId()).isEqualTo(1L);
    }
}
