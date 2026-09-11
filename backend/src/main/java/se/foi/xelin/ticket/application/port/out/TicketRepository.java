package se.foi.xelin.ticket.application.port.out;

import se.foi.xelin.ticket.domain.model.Ticket;

import java.util.List;
import java.util.Optional;

// Vad behöver systemet från omvärlden? Beständig lagring av ärenden.
public interface TicketRepository {
    Ticket save(Ticket ticket);

    List<Ticket> findAll();

    Optional<Ticket> findById(Long id);
}
