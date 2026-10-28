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

import com.mamroune.meetingbook.domain.ReservationStatus;
import com.mamroune.meetingbook.exception.ReservationConflictException;
import com.mamroune.meetingbook.domain.AppUser;
import com.mamroune.meetingbook.repository.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReservationService {

        private final ReservationRepository reservationRepository;
        private final MeetingRoomRepository meetingRoomRepository;
        private final ReservationMapper reservationMapper;
        private final AppUserRepository appUserRepository;

        public ReservationService(
                        ReservationRepository reservationRepository,
                        MeetingRoomRepository meetingRoomRepository,
                        ReservationMapper reservationMapper,
                        AppUserRepository appUserRepository) {
                this.reservationRepository = reservationRepository;
                this.meetingRoomRepository = meetingRoomRepository;
                this.reservationMapper = reservationMapper;
                this.appUserRepository = appUserRepository;
        }

        public List<ReservationResponse> getAllReservations(String email) {
                return reservationRepository.findByOwner_Email(email)
                                .stream()
                                .map(reservationMapper::toResponse)
                                .toList();
        }

        public ReservationResponse getReservationById(Long id, String email) {
                Reservation reservation = reservationRepository
                                .findByIdAndOwner_Email(id, email)
                                .orElseThrow(() -> new ReservationNotFoundException(id));

                return reservationMapper.toResponse(reservation);
        }

        public ReservationResponse createReservation(
                        ReservationRequest request, String email) {
                if (!request.getEndAt().isAfter(request.getStartAt())) {
                        throw new InvalidReservationPeriodException();
                }
                MeetingRoom meetingRoom = meetingRoomRepository.findById(request.getMeetingRoomId())
                                .orElseThrow(() -> new MeetingRoomNotFoundException(request.getMeetingRoomId()));

                boolean hasConflict = reservationRepository
                                .existsByMeetingRoom_IdAndStatusAndStartAtLessThanAndEndAtGreaterThan(
                                                request.getMeetingRoomId(),
                                                ReservationStatus.ACTIVE,
                                                request.getEndAt(),
                                                request.getStartAt());

                if (hasConflict) {
                        throw new ReservationConflictException();
                }
                AppUser owner = appUserRepository.findByEmail(email)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"));
                Reservation reservation = new Reservation(
                                meetingRoom,
                                request.getStartAt(),
                                request.getEndAt(),
                                owner);

                Reservation savedReservation = reservationRepository.save(reservation);

                return reservationMapper.toResponse(savedReservation);
        }

        public ReservationResponse cancelReservation(Long id, String email) {
                Reservation reservation = reservationRepository
                                .findByIdAndOwner_Email(id, email)
                                .orElseThrow(() -> new ReservationNotFoundException(id));

                reservation.cancel();

                Reservation savedReservation = reservationRepository.save(reservation);

                return reservationMapper.toResponse(savedReservation);
        }
}
