package com.mamroune.meetingbook.repository;

import com.mamroune.meetingbook.domain.MeetingRoom;
import com.mamroune.meetingbook.domain.Reservation;
import com.mamroune.meetingbook.domain.ReservationStatus;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MeetingRoomRepository meetingRoomRepository;

    @Test
    void findsOnlyOverlappingActiveReservationsInSameRoom() {
        MeetingRoom room = meetingRoomRepository.saveAndFlush(
                new MeetingRoom("Test Room A", "Floor 1", 8, Set.of())
        );

        MeetingRoom otherRoom = meetingRoomRepository.saveAndFlush(
                new MeetingRoom("Test Room B", "Floor 1", 8, Set.of())
        );

        LocalDateTime startAt = LocalDateTime.now()
                .plusDays(30)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        LocalDateTime endAt = startAt.plusHours(1);

        Reservation reservation = reservationRepository.saveAndFlush(
                new Reservation(room, startAt, endAt)
        );

        // An overlapping reservation must be rejected.
        assertTrue(reservationRepository
                .existsByMeetingRoom_IdAndStatusAndStartAtLessThanAndEndAtGreaterThan(
                        room.getId(),
                        ReservationStatus.ACTIVE,
                        endAt.plusMinutes(30),
                        startAt.plusMinutes(30)
                ));

        // Consecutive reservations are allowed.
        assertFalse(reservationRepository
                .existsByMeetingRoom_IdAndStatusAndStartAtLessThanAndEndAtGreaterThan(
                        room.getId(),
                        ReservationStatus.ACTIVE,
                        endAt.plusHours(1),
                        endAt
                ));

        // Another meeting room is not affected.
        assertFalse(reservationRepository
                .existsByMeetingRoom_IdAndStatusAndStartAtLessThanAndEndAtGreaterThan(
                        otherRoom.getId(),
                        ReservationStatus.ACTIVE,
                        endAt.plusMinutes(30),
                        startAt.plusMinutes(30)
                ));

        // A cancelled reservation no longer blocks the time slot.
        reservation.cancel();
        reservationRepository.saveAndFlush(reservation);

        assertFalse(reservationRepository
                .existsByMeetingRoom_IdAndStatusAndStartAtLessThanAndEndAtGreaterThan(
                        room.getId(),
                        ReservationStatus.ACTIVE,
                        endAt.plusMinutes(30),
                        startAt.plusMinutes(30)
                ));
    }
}