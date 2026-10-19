package com.mamroune.meetingbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mamroune.meetingbook.domain.Reservation;

import java.time.LocalDateTime;
import com.mamroune.meetingbook.domain.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByMeetingRoom_IdAndStatusAndStartAtLessThanAndEndAtGreaterThan(
            Long meetingRoomId,
            ReservationStatus status,
            LocalDateTime requestedEndAt,
            LocalDateTime requestedStartAt);
}
