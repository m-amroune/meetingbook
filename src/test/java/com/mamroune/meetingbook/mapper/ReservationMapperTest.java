package com.mamroune.meetingbook.mapper;

import com.mamroune.meetingbook.domain.MeetingRoom;
import com.mamroune.meetingbook.domain.Reservation;
import com.mamroune.meetingbook.domain.ReservationStatus;
import com.mamroune.meetingbook.dto.ReservationResponse;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReservationMapperTest {

    private final ReservationMapper mapper =
            Mappers.getMapper(ReservationMapper.class);

    @Test
    void mapsReservationToResponse() {
        MeetingRoom room = mock(MeetingRoom.class);

        when(room.getId()).thenReturn(7L);
        when(room.getName()).thenReturn("Victor Hugo");

        LocalDateTime startAt = LocalDateTime.now().plusDays(1);
        LocalDateTime endAt = startAt.plusHours(1);

        Reservation reservation = new Reservation(room, startAt, endAt);
        reservation.cancel();

        ReservationResponse response = mapper.toResponse(reservation);

        assertEquals(7L, response.getMeetingRoomId());
        assertEquals("Victor Hugo", response.getMeetingRoomName());
        assertEquals(startAt, response.getStartAt());
        assertEquals(endAt, response.getEndAt());
        assertEquals(ReservationStatus.CANCELLED, response.getStatus());
    }
}