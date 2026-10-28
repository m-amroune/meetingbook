package com.mamroune.meetingbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mamroune.meetingbook.domain.Reservation;

import java.time.LocalDateTime;
import com.mamroune.meetingbook.domain.ReservationStatus;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByMeetingRoom_IdAndStatusAndStartAtLessThanAndEndAtGreaterThan(
            Long meetingRoomId,
            ReservationStatus status,
            LocalDateTime requestedEndAt,
            LocalDateTime requestedStartAt);

    List<Reservation> findByOwner_Email(String email);

    Optional<Reservation> findByIdAndOwner_Email(Long id, String email);

    boolean existsByOwner_EmailAndStatusAndStartAtGreaterThanEqualAndStartAtLessThan(
            String email,
            ReservationStatus status,
            LocalDateTime dayStart,
            LocalDateTime nextDayStart);
}
