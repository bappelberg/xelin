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
import se.foi.xelin.ticket.application.port.in.AddTicketCommentUseCase;
import se.foi.xelin.ticket.application.port.in.ListTicketCommentsUseCase;
import se.foi.xelin.ticket.domain.model.TicketAccessDeniedException;
import se.foi.xelin.ticket.domain.model.TicketComment;
import se.foi.xelin.ticket.domain.model.TicketNotFoundException;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketCommentController.class)
@Import(MethodSecurityConfig.class)
class TicketCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddTicketCommentUseCase addComment;

    @MockitoBean
    private ListTicketCommentsUseCase listComments;

    @Test
    @WithMockUser(username = "ben", roles = "User")
    void slutanvandare_lagger_till_kommentar_ger_201() throws Exception {
        TicketComment saved = new TicketComment(5L, 1001L, "ben", "Fortfarande trasig", false, Instant.now());
        when(addComment.addComment(any())).thenReturn(saved);

        mockMvc.perform(post("/api/tickets/1001/comments").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"Fortfarande trasig\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.author").value("ben"))
                .andExpect(jsonPath("$.internal").value(false));
    }

    @Test
    @WithMockUser(username = "agnes", roles = "Agent")
    void handlaggare_kan_skapa_intern_kommentar() throws Exception {
        TicketComment saved = new TicketComment(6L, 1001L, "agnes", "Väntar på reservdel", true, Instant.now());
        when(addComment.addComment(any())).thenReturn(saved);

        mockMvc.perform(post("/api/tickets/1001/comments").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"Väntar på reservdel\",\"internal\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.internal").value(true));
    }

    @Test
    void oautentiserat_anrop_nekas() throws Exception {
        mockMvc.perform(post("/api/tickets/1001/comments").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"Test\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "ben", roles = "User")
    void tom_kommentar_ger_400() throws Exception {
        mockMvc.perform(post("/api/tickets/1001/comments").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"  \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "bob", roles = "User")
    void slutanvandare_nekas_kommentera_annans_arende() throws Exception {
        when(addComment.addComment(any())).thenThrow(new TicketAccessDeniedException(1001L));

        mockMvc.perform(post("/api/tickets/1001/comments").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"Test\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "ben", roles = "User")
    void kommentar_pa_okant_arende_ger_404() throws Exception {
        when(addComment.addComment(any())).thenThrow(new TicketNotFoundException(9999L));

        mockMvc.perform(post("/api/tickets/9999/comments").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"Test\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "agnes", roles = "Agent")
    void handlaggare_listar_kommentarer() throws Exception {
        when(listComments.listComments(any())).thenReturn(List.of(
                new TicketComment(1L, 1001L, "ben", "Publik kommentar", false, Instant.now()),
                new TicketComment(2L, 1001L, "agnes", "Intern kommentar", true, Instant.now())));

        mockMvc.perform(get("/api/tickets/1001/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
