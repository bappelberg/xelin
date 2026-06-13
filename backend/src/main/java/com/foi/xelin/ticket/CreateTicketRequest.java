// LAGER: DTO (Data Transfer Object) – representerar det inkommande formuläret från UI.
// Valideras automatiskt av Spring innan det når controllern (@Valid).
// DTO Ticket skapas aldrig direkt från HTTP-anrop.
/* 💡

Tumregel
* DTO = record
* Entity = class 

*/
package com.foi.xelin.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO kör ofta record istället för klass. Det är mycket mindre kod, man behöver inte skapa konstruktorer och getters och setters
public record CreateTicketRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 2000) String description
) {}
