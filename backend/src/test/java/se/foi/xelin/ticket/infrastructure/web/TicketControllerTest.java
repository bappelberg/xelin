package se.foi.xelin.ticket.infrastructure.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.foi.xelin.shared.security.MethodSecurityConfig;
import se.foi.xelin.ticket.application.port.in.CreateTicketUseCase;
import se.foi.xelin.ticket.application.port.in.GetTicketUseCase;
import se.foi.xelin.ticket.application.port.in.ListTicketsUseCase;
import se.foi.xelin.ticket.application.port.in.UpdateTicketUseCase;
import se.foi.xelin.ticket.domain.model.Ticket;
import se.foi.xelin.ticket.domain.model.TicketCategory;
import se.foi.xelin.ticket.domain.model.TicketNotFoundException;
import se.foi.xelin.ticket.domain.model.TicketPriority;
import se.foi.xelin.ticket.domain.model.TicketStatus;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
@Import(MethodSecurityConfig.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateTicketUseCase createTicket;

    @MockitoBean
    private ListTicketsUseCase listTickets;

    @MockitoBean
    private GetTicketUseCase getTicket;

    @MockitoBean
    private UpdateTicketUseCase updateTicket;

    private static final String BODY = """
            {"title":"Skrivaren fungerar inte","description":"Felkod E-52","priority":"NORMAL","category":"HARDWARE"}
            """;

    @Test
    @WithMockUser(username = "ben", roles = "User")
    void slutanvandare_skapar_arende_ger_201_med_id() throws Exception {
        Ticket saved = new Ticket(1001L, "Skrivaren fungerar inte", "Felkod E-52",
                TicketPriority.NORMAL, TicketCategory.HARDWARE, TicketStatus.NEW,
                "ben", Instant.parse("2026-01-01T10:00:00Z"));
        when(createTicket.create(any())).thenReturn(saved);

        mockMvc.perform(post("/api/tickets").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/tickets/1001"))
                .andExpect(jsonPath("$.id").value(1001))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    @WithMockUser(username = "agnes", roles = "Agent")
    void fel_roll_ger_403() throws Exception {
        mockMvc.perform(post("/api/tickets").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    void oautentiserat_anrop_nekas() throws Exception {
        // Exakt statuskod (401) verifieras mot den riktiga WebSecurityConfig i integrationstestet.
        mockMvc.perform(post("/api/tickets").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "ben", roles = "User")
    void tom_titel_ger_400() throws Exception {
        String utanTitel = """
                {"title":"  ","description":"Felkod E-52","priority":"NORMAL","category":"HARDWARE"}
                """;
        mockMvc.perform(post("/api/tickets").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(utanTitel))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "ben", roles = "Agent")
    void handlaggare_listar_arendekon() throws Exception {
        Ticket t = new Ticket(1001L, "Skrivaren fungerar inte", "Felkod E-52",
                TicketPriority.NORMAL, TicketCategory.HARDWARE, TicketStatus.NEW,
                "ben", Instant.parse("2026-01-01T10:00:00Z"));
        when(listTickets.listAll()).thenReturn(List.of(t));

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1001))
                .andExpect(jsonPath("$[0].title").value("Skrivaren fungerar inte"))
                .andExpect(jsonPath("$[0].status").value("NEW"));
    }

    @Test
    @WithMockUser(username = "bob", roles = "User")
    void slutanvandare_far_inte_lista_arendekon() throws Exception {
        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "ben", roles = "Agent")
    void handlaggare_hamtar_ett_arende() throws Exception {
        Ticket t = new Ticket(1001L, "Skrivaren fungerar inte", "Felkod E-52",
                TicketPriority.NORMAL, TicketCategory.HARDWARE, TicketStatus.NEW,
                "ben", Instant.parse("2026-01-01T10:00:00Z"));
        when(getTicket.getById(1001L)).thenReturn(t);

        mockMvc.perform(get("/api/tickets/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Skrivaren fungerar inte"))
                .andExpect(jsonPath("$.description").value("Felkod E-52"));
    }

    @Test
    @WithMockUser(username = "ben", roles = "Agent")
    void okant_arende_ger_404() throws Exception {
        when(getTicket.getById(9999L)).thenThrow(new TicketNotFoundException(9999L));

        mockMvc.perform(get("/api/tickets/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "ben", roles = "Agent")
    void handlaggare_uppdaterar_status_och_prioritet() throws Exception {
        Ticket updated = new Ticket(1001L, "Skrivaren fungerar inte", "Felkod E-52",
                TicketPriority.HIGH, TicketCategory.HARDWARE, TicketStatus.ASSIGNED,
                "ben", Instant.parse("2026-01-01T10:00:00Z"));
        when(updateTicket.update(any())).thenReturn(updated);

        mockMvc.perform(patch("/api/tickets/1001").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ASSIGNED\",\"priority\":\"HIGH\",\"category\":\"HARDWARE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ASSIGNED"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    @WithMockUser(username = "bob", roles = "User")
    void slutanvandare_far_inte_uppdatera_arende() throws Exception {
        mockMvc.perform(patch("/api/tickets/1001").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ASSIGNED\",\"priority\":\"HIGH\",\"category\":\"HARDWARE\"}"))
                .andExpect(status().isForbidden());
    }
}
