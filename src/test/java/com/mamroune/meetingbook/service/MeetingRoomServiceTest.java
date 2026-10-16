package com.mamroune.meetingbook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mamroune.meetingbook.domain.MeetingRoom;
import com.mamroune.meetingbook.dto.MeetingRoomResponse;
import com.mamroune.meetingbook.exception.MeetingRoomNotFoundException;
import com.mamroune.meetingbook.mapper.MeetingRoomMapper;
import com.mamroune.meetingbook.repository.MeetingRoomRepository;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.mamroune.meetingbook.dto.MeetingRoomRequest;

@ExtendWith(MockitoExtension.class)
class MeetingRoomServiceTest {

    @Mock
    private MeetingRoomRepository meetingRoomRepository;

    @Mock
    private MeetingRoomMapper meetingRoomMapper;

    @InjectMocks
    private MeetingRoomService meetingRoomService;

    @Test
    void shouldReturnAllMeetingRooms() {
        MeetingRoom meetingRoom = new MeetingRoom(
                "Victor Hugo",
                "Etage 1",
                8,
                Set.of("Ecran"));

        MeetingRoomResponse response = new MeetingRoomResponse();
        response.setName("Victor Hugo");

        when(meetingRoomRepository.findAll()).thenReturn(List.of(meetingRoom));
        when(meetingRoomMapper.toResponse(meetingRoom)).thenReturn(response);

        List<MeetingRoomResponse> result = meetingRoomService.getAllMeetingRooms();

        assertEquals(1, result.size());
        assertEquals("Victor Hugo", result.get(0).getName());

        verify(meetingRoomRepository).findAll();
        verify(meetingRoomMapper).toResponse(meetingRoom);
    }

    @Test
    void shouldReturnMeetingRoomById() {
        MeetingRoom meetingRoom = new MeetingRoom(
                "Simone Veil",
                "Etage 2",
                12,
                Set.of("Ecran"));

        MeetingRoomResponse response = new MeetingRoomResponse();
        response.setName("Simone Veil");

        when(meetingRoomRepository.findById(1L)).thenReturn(Optional.of(meetingRoom));
        when(meetingRoomMapper.toResponse(meetingRoom)).thenReturn(response);

        MeetingRoomResponse result = meetingRoomService.getMeetingRoomById(1L);

        assertEquals("Simone Veil", result.getName());

        verify(meetingRoomRepository).findById(1L);
        verify(meetingRoomMapper).toResponse(meetingRoom);
    }

    @Test
    void shouldThrowWhenMeetingRoomDoesNotExist() {
        when(meetingRoomRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                MeetingRoomNotFoundException.class,
                () -> meetingRoomService.getMeetingRoomById(999L));

        verify(meetingRoomRepository).findById(999L);
    }

    @Test
    void shouldCreateMeetingRoom() {
        MeetingRoomRequest request = new MeetingRoomRequest();
        request.setName("Louis Pasteur");
        request.setLocation("Etage 3");
        request.setCapacity(6);
        request.setEquipment(Set.of("Ecran"));

        MeetingRoom meetingRoom = new MeetingRoom(
                "Louis Pasteur",
                "Etage 3",
                6,
                Set.of("Ecran"));

        MeetingRoomResponse response = new MeetingRoomResponse();
        response.setName("Louis Pasteur");

        when(meetingRoomMapper.toEntity(request)).thenReturn(meetingRoom);
        when(meetingRoomRepository.save(meetingRoom)).thenReturn(meetingRoom);
        when(meetingRoomMapper.toResponse(meetingRoom)).thenReturn(response);

        MeetingRoomResponse result = meetingRoomService.createMeetingRoom(request);

        assertEquals("Louis Pasteur", result.getName());

        verify(meetingRoomMapper).toEntity(request);
        verify(meetingRoomRepository).save(meetingRoom);
        verify(meetingRoomMapper).toResponse(meetingRoom);
    }
@Test
void shouldUpdateMeetingRoom() {
    MeetingRoomRequest request = new MeetingRoomRequest();
    request.setName("Jean Moulin");
    request.setLocation("Etage 2");
    request.setCapacity(10);
    request.setEquipment(Set.of("Ecran"));

    MeetingRoom meetingRoom = new MeetingRoom(
            "Jean Moulin",
            "Etage 1",
            8,
            Set.of("Tableau blanc"));

    MeetingRoomResponse response = new MeetingRoomResponse();
    response.setName("Jean Moulin");
    response.setLocation("Etage 2");
    response.setCapacity(10);

    when(meetingRoomRepository.findById(1L))
            .thenReturn(Optional.of(meetingRoom));

    when(meetingRoomRepository.save(meetingRoom))
            .thenReturn(meetingRoom);

    when(meetingRoomMapper.toResponse(meetingRoom))
            .thenReturn(response);

    MeetingRoomResponse result =
            meetingRoomService.updateMeetingRoom(1L, request);

    assertEquals("Jean Moulin", result.getName());
    assertEquals("Etage 2", result.getLocation());
    assertEquals(10, result.getCapacity());

    verify(meetingRoomRepository).findById(1L);
    verify(meetingRoomMapper).updateEntity(request, meetingRoom);
    verify(meetingRoomRepository).save(meetingRoom);
    verify(meetingRoomMapper).toResponse(meetingRoom);
}
@Test
void shouldDeleteMeetingRoom() {
    MeetingRoom meetingRoom = new MeetingRoom(
            "Olympe de Gouges",
            "Etage 1",
            6,
            Set.of("Ecran"));

    when(meetingRoomRepository.findById(1L))
            .thenReturn(Optional.of(meetingRoom));

    meetingRoomService.deleteMeetingRoom(1L);

    verify(meetingRoomRepository).findById(1L);
    verify(meetingRoomRepository).delete(meetingRoom);
}
}