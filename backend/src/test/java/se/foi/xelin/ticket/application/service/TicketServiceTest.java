package se.foi.xelin.ticket.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.foi.xelin.ticket.application.port.in.CreateTicketCommand;
import se.foi.xelin.ticket.application.port.out.TicketRepository;
import se.foi.xelin.ticket.domain.model.Ticket;
import se.foi.xelin.ticket.domain.model.TicketCategory;
import se.foi.xelin.ticket.domain.model.TicketPriority;
import se.foi.xelin.ticket.domain.model.TicketStatus;

import static org.assertj.core.api.Assertions.assertThat;
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
    void nytt_arende_far_status_NY_och_reporter_fran_kommandot() {
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket t = invocation.getArgument(0);
            return new Ticket(42L, t.getTitle(), t.getDescription(), t.getPriority(),
                    t.getCategory(), t.getStatus(), t.getReporter(), t.getCreatedAt());
        });

        CreateTicketCommand command = new CreateTicketCommand(
                "ben", "Skrivaren fungerar inte", "Får felkod E-52",
                TicketPriority.NORMAL, TicketCategory.HARDVARA);

        Ticket result = ticketService.create(command);

        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getStatus()).isEqualTo(TicketStatus.NY);
        assertThat(result.getReporter()).isEqualTo("ben");
    }

    @Test
    void arendet_sparas_utan_id_och_med_skapandetidpunkt() {
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        ticketService.create(new CreateTicketCommand(
                "bob", "Kan inte logga in", "Kontot verkar låst",
                TicketPriority.HOG, TicketCategory.KONTO));

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(captor.capture());

        Ticket persisted = captor.getValue();
        assertThat(persisted.getId()).isNull();
        assertThat(persisted.getStatus()).isEqualTo(TicketStatus.NY);
        assertThat(persisted.getCreatedAt()).isNotNull();
    }
}
