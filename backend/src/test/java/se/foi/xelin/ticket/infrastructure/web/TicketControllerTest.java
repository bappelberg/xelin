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
import se.foi.xelin.ticket.domain.model.Ticket;
import se.foi.xelin.ticket.domain.model.TicketCategory;
import se.foi.xelin.ticket.domain.model.TicketPriority;
import se.foi.xelin.ticket.domain.model.TicketStatus;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

    private static final String BODY = """
            {"title":"Skrivaren fungerar inte","description":"Felkod E-52","priority":"NORMAL","category":"HARDVARA"}
            """;

    @Test
    @WithMockUser(username = "ben", roles = "User")
    void slutanvandare_skapar_arende_ger_201_med_id() throws Exception {
        Ticket saved = new Ticket(1001L, "Skrivaren fungerar inte", "Felkod E-52",
                TicketPriority.NORMAL, TicketCategory.HARDVARA, TicketStatus.NY,
                "ben", Instant.parse("2026-01-01T10:00:00Z"));
        when(createTicket.create(any())).thenReturn(saved);

        mockMvc.perform(post("/api/tickets").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/tickets/1001"))
                .andExpect(jsonPath("$.id").value(1001))
                .andExpect(jsonPath("$.status").value("NY"));
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
                {"title":"  ","description":"Felkod E-52","priority":"NORMAL","category":"HARDVARA"}
                """;
        mockMvc.perform(post("/api/tickets").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(utanTitel))
                .andExpect(status().isBadRequest());
    }
}
