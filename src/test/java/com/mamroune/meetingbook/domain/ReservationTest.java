package com.mamroune.meetingbook.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class ReservationTest {

    @Test
    void shouldCreateActiveReservation() {
        MeetingRoom room = new MeetingRoom(
            "Simone Veil",
            "1er étage",
            6,
            Set.of("Écran")
        );

        LocalDateTime startAt = LocalDateTime.of(2026, 10, 15, 14, 0);
        LocalDateTime endAt = LocalDateTime.of(2026, 10, 15, 16, 0);

        Reservation reservation = new Reservation(room, startAt, endAt);

        assertEquals(room, reservation.getMeetingRoom());
        assertEquals(startAt, reservation.getStartAt());
        assertEquals(endAt, reservation.getEndAt());
        assertEquals(ReservationStatus.ACTIVE, reservation.getStatus());
    }
}