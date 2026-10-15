package com.mamroune.meetingbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mamroune.meetingbook.domain.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
