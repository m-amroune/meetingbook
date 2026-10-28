
package com.mamroune.meetingbook.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.mock.web.MockHttpSession;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void allowsPublicAccessToMeetingRooms() throws Exception {
        mockMvc.perform(get("/api/meeting-rooms"))
                .andExpect(status().isOk());
    }

    @Test
    void rejectsUnauthenticatedAccessToCurrentUser() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsMeetingRoomCreation() throws Exception {
        mockMvc.perform(post("/api/meeting-rooms")
                .with(user("test@example.com").roles("USER"))
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void allowsAuthenticatedAccessToCurrentUser() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .with(user("test@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void invalidatesSessionOnLogout() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/auth/logout")
                .session(session)
                .with(user("test@example.com").roles("USER"))
                .with(csrf()))
                .andExpect(status().isNoContent());

        assertTrue(session.isInvalid());
    }

    @Test
    void rejectsUnauthenticatedAccessToReservations() throws Exception {
        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isUnauthorized());
    }
    @Test
void allowsAuthenticatedAccessToReservations() throws Exception {
    mockMvc.perform(get("/api/reservations")
            .with(user("test@example.com").roles("USER")))
            .andExpect(status().isOk());
}
@Test
void rejectsUnauthenticatedReservationCancellation() throws Exception {
    mockMvc.perform(patch("/api/reservations/1/cancel")
            .with(csrf()))
            .andExpect(status().isUnauthorized());
}
}
