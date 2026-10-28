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
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;

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
                var startAt = request.getStartAt();
                var endAt = request.getEndAt();

                // Reservations are allowed from Monday to Saturday.
                if (startAt.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Les réservations sont interdites le dimanche");
                }

                // Reservations must start and end on the same day, between 9:00 and 19:00.
                if (!startAt.toLocalDate().equals(endAt.toLocalDate())
                                || startAt.toLocalTime().isBefore(LocalTime.of(9, 0))
                                || endAt.toLocalTime().isAfter(LocalTime.of(19, 0))) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Les réservations doivent être comprises entre 9 h et 19 h");
                }

                // Start and end times must align with 30-minute slots.
                if (startAt.getMinute() % 30 != 0
                                || endAt.getMinute() % 30 != 0
                                || startAt.getSecond() != 0
                                || endAt.getSecond() != 0
                                || startAt.getNano() != 0
                                || endAt.getNano() != 0) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Les horaires doivent respecter des créneaux de 30 minutes");
                }

                // Duration must be between 1 and 3 hours, in 30-minute increments.
                long durationMinutes = Duration.between(startAt, endAt).toMinutes();

                if (durationMinutes < 60
                                || durationMinutes > 180
                                || durationMinutes % 30 != 0) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "La durée doit être comprise entre 1 h et 3 h, par pas de 30 minutes");
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
                // Allow only one active reservation per user and day.
var dayStart = request.getStartAt().toLocalDate().atStartOfDay();
var nextDayStart = dayStart.plusDays(1);

boolean hasDailyReservation = reservationRepository
        .existsByOwner_EmailAndStatusAndStartAtGreaterThanEqualAndStartAtLessThan(
                email,
                ReservationStatus.ACTIVE,
                dayStart,
                nextDayStart);

if (hasDailyReservation) {
    throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            "Vous avez déjà une réservation active pour cette journée");
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
