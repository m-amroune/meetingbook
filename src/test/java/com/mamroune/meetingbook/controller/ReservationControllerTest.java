package com.mamroune.meetingbook.controller;

import com.mamroune.meetingbook.dto.ReservationResponse;
import com.mamroune.meetingbook.service.ReservationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.mamroune.meetingbook.exception.ReservationNotFoundException;
import com.mamroune.meetingbook.dto.ReservationRequest;
import java.time.LocalDateTime;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import com.mamroune.meetingbook.domain.ReservationStatus;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ReservationController(reservationService))
                .build();
    }

    @Test
    void getAllReservationsReturnsOkAndReservations() throws Exception {
        ReservationResponse response = new ReservationResponse();
        response.setId(1L);
        response.setMeetingRoomId(2L);

        when(reservationService.getAllReservations())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].meetingRoomId").value(2));
    }

    @Test
    void getReservationByIdReturnsOkAndReservation() throws Exception {
        Long id = 1L;

        ReservationResponse response = new ReservationResponse();
        response.setId(id);
        response.setMeetingRoomId(2L);
        response.setMeetingRoomName("Victor Hugo");

        when(reservationService.getReservationById(id))
                .thenReturn(response);

        mockMvc.perform(get("/api/reservations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.meetingRoomId").value(2))
                .andExpect(jsonPath("$.meetingRoomName").value("Victor Hugo"));
    }

    @Test
    void getReservationByIdReturnsNotFoundWhenReservationDoesNotExist() throws Exception {
        Long id = 999L;

        when(reservationService.getReservationById(id))
                .thenThrow(new ReservationNotFoundException(id));

        mockMvc.perform(get("/api/reservations/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReservationReturnsCreatedAndReservation() throws Exception {
        LocalDateTime startAt = LocalDateTime.now().plusDays(1).withNano(0);
        LocalDateTime endAt = startAt.plusHours(1);

        String requestBody = """
                {
                    "meetingRoomId": 2,
                    "startAt": "%s",
                    "endAt": "%s"
                }
                """.formatted(startAt, endAt);

        ReservationResponse response = new ReservationResponse();
        response.setId(5L);
        response.setMeetingRoomId(2L);
        response.setMeetingRoomName("Victor Hugo");

        when(reservationService.createReservation(any(ReservationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.meetingRoomId").value(2))
                .andExpect(jsonPath("$.meetingRoomName").value("Victor Hugo"));
    }

    @Test
    void cancelReservationReturnsOkAndCancelledReservation() throws Exception {
        Long id = 1L;

        ReservationResponse response = new ReservationResponse();
        response.setId(id);
        response.setMeetingRoomId(2L);
        response.setStatus(ReservationStatus.CANCELLED);

        when(reservationService.cancelReservation(id))
                .thenReturn(response);

        mockMvc.perform(patch("/api/reservations/{id}/cancel", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void createReservationReturnsBadRequestWhenRoomIdIsMissing() throws Exception {
        LocalDateTime startAt = LocalDateTime.now().plusDays(1).withNano(0);
        LocalDateTime endAt = startAt.plusHours(1);

        String requestBody = """
                {
                    "startAt": "%s",
                    "endAt": "%s"
                }
                """.formatted(startAt, endAt);

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(reservationService);
    }
}
