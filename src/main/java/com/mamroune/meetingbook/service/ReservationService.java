package com.mamroune.meetingbook.service;

import org.springframework.stereotype.Service;

import com.mamroune.meetingbook.mapper.ReservationMapper;
import com.mamroune.meetingbook.repository.MeetingRoomRepository;
import com.mamroune.meetingbook.repository.ReservationRepository;

import java.util.List;
import com.mamroune.meetingbook.dto.ReservationResponse;

import com.mamroune.meetingbook.domain.Reservation;

import com.mamroune.meetingbook.domain.MeetingRoom;
import com.mamroune.meetingbook.dto.ReservationRequest;
import com.mamroune.meetingbook.exception.MeetingRoomNotFoundException;
import com.mamroune.meetingbook.exception.ReservationNotFoundException;
import com.mamroune.meetingbook.exception.InvalidReservationPeriodException;

@Service
public class ReservationService {

        private final ReservationRepository reservationRepository;
        private final MeetingRoomRepository meetingRoomRepository;
        private final ReservationMapper reservationMapper;

        public ReservationService(
                        ReservationRepository reservationRepository,
                        MeetingRoomRepository meetingRoomRepository,
                        ReservationMapper reservationMapper) {
                this.reservationRepository = reservationRepository;
                this.meetingRoomRepository = meetingRoomRepository;
                this.reservationMapper = reservationMapper;
        }

        public List<ReservationResponse> getAllReservations() {
                return reservationRepository.findAll()
                                .stream()
                                .map(reservationMapper::toResponse)
                                .toList();
        }

        public ReservationResponse getReservationById(Long id) {
                Reservation reservation = reservationRepository.findById(id)
                                .orElseThrow(() -> new ReservationNotFoundException(id));

                return reservationMapper.toResponse(reservation);
        }

        public ReservationResponse createReservation(ReservationRequest request) {
                if (!request.getEndAt().isAfter(request.getStartAt())) {
                        throw new InvalidReservationPeriodException();
                }
                MeetingRoom meetingRoom = meetingRoomRepository.findById(request.getMeetingRoomId())
                                .orElseThrow(() -> new MeetingRoomNotFoundException(request.getMeetingRoomId()));

                Reservation reservation = new Reservation(
                                meetingRoom,
                                request.getStartAt(),
                                request.getEndAt());

                Reservation savedReservation = reservationRepository.save(reservation);

                return reservationMapper.toResponse(savedReservation);
        }

        public ReservationResponse cancelReservation(Long id) {
                Reservation reservation = reservationRepository.findById(id)
                                .orElseThrow(() -> new ReservationNotFoundException(id));

                reservation.cancel();

                Reservation savedReservation = reservationRepository.save(reservation);

                return reservationMapper.toResponse(savedReservation);
        }
}
