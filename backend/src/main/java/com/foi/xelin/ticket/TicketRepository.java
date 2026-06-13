// LAGER: Repository – enda platsen som pratar med databasen.
// Spring Data JPA genererar SQL automatiskt utifrån metodnamn.
// Servicelagret anropar detta; controllern gör det aldrig direkt.
package com.foi.xelin.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
// Repository<T, ID> = (Entity, Primary Key Type)
interface TicketRepository extends JpaRepository<Ticket, Long> {}
