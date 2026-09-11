package se.foi.xelin.ticket.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.foi.xelin.ticket.application.port.in.CreateTicketCommand;
import se.foi.xelin.ticket.application.port.in.CreateTicketUseCase;
import se.foi.xelin.ticket.application.port.in.GetTicketUseCase;
import se.foi.xelin.ticket.application.port.in.ListTicketsUseCase;
import se.foi.xelin.ticket.application.port.in.UpdateTicketCommand;
import se.foi.xelin.ticket.application.port.in.UpdateTicketUseCase;
import se.foi.xelin.ticket.application.port.out.TicketRepository;
import se.foi.xelin.ticket.domain.model.Ticket;
import se.foi.xelin.ticket.domain.model.TicketNotFoundException;

import java.util.List;

@Service
public class TicketService implements CreateTicketUseCase, ListTicketsUseCase, GetTicketUseCase, UpdateTicketUseCase {

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

    @Override
    public List<Ticket> listAll() {
        return ticketRepository.findAll();
    }

    @Override
    public Ticket getById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    @Override
    public Ticket update(UpdateTicketCommand command) {
        Ticket ticket = getById(command.ticketId());
        Ticket updated = ticket.update(command.status(), command.priority(), command.category());
        Ticket saved = ticketRepository.save(updated);

        // TODO (audit): emittera TICKET_UPDATED via audit-kontexten när den finns — se skill audit-event.
        log.info("Ärende uppdaterat: id={} status={} priority={} kategori={}",
                saved.getId(), saved.getStatus(), saved.getPriority(), saved.getCategory());

        return saved;
    }
}
