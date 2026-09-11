package se.foi.xelin.ticket.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.foi.xelin.ticket.application.port.in.CreateTicketCommand;
import se.foi.xelin.ticket.application.port.in.CreateTicketUseCase;
import se.foi.xelin.ticket.application.port.out.TicketRepository;
import se.foi.xelin.ticket.domain.model.Ticket;

@Service
public class TicketService implements CreateTicketUseCase {

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Ticket create(CreateTicketCommand command) {
        Ticket ticket = Ticket.create(
                command.title(),
                command.description(),
                command.priority(),
                command.category(),
                command.reporter());

        Ticket saved = ticketRepository.save(ticket);

        // KR-202: unikt ärende-ID tilldelas av databasen vid skapande.
        log.info("Ärende skapat: id={} reporter={} priority={} kategori={}",
                saved.getId(), saved.getReporter(), saved.getPriority(), saved.getCategory());

        // TODO (audit): emittera TICKET_CREATED via audit-kontexten när den finns — se skill audit-event.
        return saved;
    }
}
