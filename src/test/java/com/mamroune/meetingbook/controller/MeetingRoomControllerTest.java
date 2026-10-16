package com.mamroune.meetingbook.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mamroune.meetingbook.dto.MeetingRoomResponse;
import com.mamroune.meetingbook.service.MeetingRoomService;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.http.MediaType;

import com.mamroune.meetingbook.dto.MeetingRoomRequest;

@WebMvcTest(MeetingRoomController.class)
class MeetingRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MeetingRoomService meetingRoomService;

    @Test
    void shouldReturnAllMeetingRooms() throws Exception {
        MeetingRoomResponse response = new MeetingRoomResponse();
        response.setName("Victor Hugo");

        when(meetingRoomService.getAllMeetingRooms())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/meeting-rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Victor Hugo"));
    }
    @Test
void shouldCreateMeetingRoom() throws Exception {
    MeetingRoomResponse response = new MeetingRoomResponse();
    response.setId(1L);
    response.setName("Victor Hugo");
    response.setLocation("Etage 1");
    response.setCapacity(8);

    when(meetingRoomService.createMeetingRoom(any(MeetingRoomRequest.class)))
            .thenReturn(response);

    mockMvc.perform(post("/api/meeting-rooms")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                      "name": "Victor Hugo",
                      "location": "Etage 1",
                      "capacity": 8,
                      "equipment": ["Ecran"]
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Victor Hugo"));
}
@Test
void shouldReturnBadRequestWhenCreatingInvalidMeetingRoom() throws Exception {
    mockMvc.perform(post("/api/meeting-rooms")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                      "name": "",
                      "location": "Etage 1",
                      "capacity": 0,
                      "equipment": []
                    }
                    """))
            .andExpect(status().isBadRequest());
}
}
