package com.mamroune.meetingbook.service;

import com.mamroune.meetingbook.domain.Reservation;
import com.mamroune.meetingbook.dto.ReservationResponse;
import com.mamroune.meetingbook.mapper.ReservationMapper;
import com.mamroune.meetingbook.repository.MeetingRoomRepository;
import com.mamroune.meetingbook.repository.ReservationRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import com.mamroune.meetingbook.exception.ReservationNotFoundException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.mamroune.meetingbook.domain.MeetingRoom;
import com.mamroune.meetingbook.dto.ReservationRequest;
import org.mockito.ArgumentCaptor;
import java.time.LocalDateTime;
import com.mamroune.meetingbook.exception.InvalidReservationPeriodException;
import com.mamroune.meetingbook.exception.MeetingRoomNotFoundException;
import com.mamroune.meetingbook.domain.ReservationStatus;

import com.mamroune.meetingbook.exception.ReservationConflictException;
import com.mamroune.meetingbook.repository.AppUserRepository;
import com.mamroune.meetingbook.domain.AppUser;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)

class ReservationServiceTest {

        private static final String EMAIL = "test@example.com";

        private static LocalDateTime validStartAt() {
                return LocalDate.now()
                                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                                .atTime(10, 0);
        }

        @Mock
        private ReservationRepository reservationRepository;

        @Mock
        private MeetingRoomRepository meetingRoomRepository;

        @Mock
        private ReservationMapper reservationMapper;

        @Mock
        private AppUserRepository appUserRepository;

        @InjectMocks
        private ReservationService reservationService;

        @Test
        void getAllReservationsReturnsMappedReservations() {
                Reservation reservation = mock(Reservation.class);
                ReservationResponse response = new ReservationResponse();

                when(reservationRepository.findByOwner_Email(EMAIL)).thenReturn(List.of(reservation));
                when(reservationMapper.toResponse(reservation)).thenReturn(response);

                List<ReservationResponse> result = reservationService.getAllReservations(EMAIL);

                assertEquals(1, result.size());
                assertSame(response, result.get(0));

                verify(reservationRepository).findByOwner_Email(EMAIL);
                ;
                verify(reservationMapper).toResponse(reservation);
        }

        @Test
        void getReservationByIdReturnsMappedReservation() {
                Long id = 1L;
                Reservation reservation = mock(Reservation.class);
                ReservationResponse response = new ReservationResponse();

                when(reservationRepository.findByIdAndOwner_Email(id, EMAIL))
                                .thenReturn(Optional.of(reservation));
                when(reservationMapper.toResponse(reservation))
                                .thenReturn(response);

                ReservationResponse result = reservationService.getReservationById(id, EMAIL);

                assertSame(response, result);

                verify(reservationRepository).findByIdAndOwner_Email(id, EMAIL);
                ;
                verify(reservationMapper).toResponse(reservation);
        }

        @Test
        void getReservationByIdThrowsWhenReservationNotFound() {
                Long id = 999L;

                when(reservationRepository.findByIdAndOwner_Email(id, EMAIL))
                                .thenReturn(Optional.empty());

                assertThrows(
                                ReservationNotFoundException.class,
                                () -> reservationService.getReservationById(id, EMAIL));

                verify(reservationRepository).findByIdAndOwner_Email(id, EMAIL);
                ;
                verifyNoInteractions(reservationMapper);
        }

        @Test
        void createReservationSavesAndReturnsMappedReservation() {
                Long roomId = 1L;
                LocalDateTime startAt = validStartAt();
                LocalDateTime endAt = startAt.plusHours(1);

                ReservationRequest request = new ReservationRequest();
                request.setMeetingRoomId(roomId);
                request.setStartAt(startAt);
                request.setEndAt(endAt);

                MeetingRoom meetingRoom = mock(MeetingRoom.class);
                ReservationResponse response = new ReservationResponse();

                AppUser owner = new AppUser(EMAIL, "hashed-password");

                when(appUserRepository.findByEmail(EMAIL))
                                .thenReturn(Optional.of(owner));

                when(meetingRoomRepository.findById(roomId))
                                .thenReturn(Optional.of(meetingRoom));

                when(reservationRepository.save(any(Reservation.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                when(reservationMapper.toResponse(any(Reservation.class)))
                                .thenReturn(response);

                ReservationResponse result = reservationService.createReservation(request, EMAIL);

                assertSame(response, result);

                ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);

                verify(reservationRepository).save(captor.capture());

                Reservation savedReservation = captor.getValue();

                assertSame(meetingRoom, savedReservation.getMeetingRoom());
                assertEquals(startAt, savedReservation.getStartAt());
                assertEquals(endAt, savedReservation.getEndAt());
                assertSame(owner, savedReservation.getOwner());

                verify(reservationMapper).toResponse(savedReservation);
        }

        @Test
        void createReservationThrowsWhenEndIsBeforeStart() {
                LocalDateTime startAt = LocalDateTime.now().plusDays(1);
                LocalDateTime endAt = startAt.minusHours(1);

                ReservationRequest request = new ReservationRequest();
                request.setMeetingRoomId(1L);
                request.setStartAt(startAt);
                request.setEndAt(endAt);

                assertThrows(
                                InvalidReservationPeriodException.class,
                                () -> reservationService.createReservation(request, EMAIL));

                verifyNoInteractions(
                                meetingRoomRepository,
                                reservationRepository,
                                reservationMapper);
        }

        @Test
        void createReservationThrowsWhenMeetingRoomNotFound() {
                Long roomId = 999L;
                LocalDateTime startAt = validStartAt();

                ReservationRequest request = new ReservationRequest();
                request.setMeetingRoomId(roomId);
                request.setStartAt(startAt);
                request.setEndAt(startAt.plusHours(1));

                when(meetingRoomRepository.findById(roomId))
                                .thenReturn(Optional.empty());

                assertThrows(
                                MeetingRoomNotFoundException.class,
                                () -> reservationService.createReservation(request, EMAIL));

                verify(meetingRoomRepository).findById(roomId);
                verifyNoInteractions(reservationRepository, reservationMapper);
        }

        @Test
        void cancelReservationUpdatesStatusAndReturnsMappedReservation() {
                Long id = 1L;
                LocalDateTime startAt = LocalDateTime.now().plusDays(1);

                MeetingRoom meetingRoom = mock(MeetingRoom.class);
                Reservation reservation = new Reservation(
                                meetingRoom,
                                startAt,
                                startAt.plusHours(1));

                ReservationResponse response = new ReservationResponse();

                when(reservationRepository.findByIdAndOwner_Email(id, EMAIL))
                                .thenReturn(Optional.of(reservation));

                when(reservationRepository.save(reservation))
                                .thenReturn(reservation);

                when(reservationMapper.toResponse(reservation))
                                .thenReturn(response);

                assertEquals(ReservationStatus.ACTIVE, reservation.getStatus());

                ReservationResponse result = reservationService.cancelReservation(id, EMAIL);

                assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
                assertSame(response, result);

                verify(reservationRepository).findByIdAndOwner_Email(id, EMAIL);
                verify(reservationRepository).save(reservation);
                verify(reservationMapper).toResponse(reservation);
        }

        @Test
        void cancelReservationThrowsWhenReservationNotFound() {
                Long id = 999L;

                when(reservationRepository.findByIdAndOwner_Email(id, EMAIL))
                                .thenReturn(Optional.empty());

                assertThrows(
                                ReservationNotFoundException.class,
                                () -> reservationService.cancelReservation(id, EMAIL));

                verify(reservationRepository).findByIdAndOwner_Email(id, EMAIL);
                verifyNoMoreInteractions(reservationRepository);
                verifyNoInteractions(meetingRoomRepository, reservationMapper);
        }

        @Test
        void createReservationThrowsWhenTimeSlotOverlaps() {
                Long roomId = 1L;
                LocalDateTime startAt = validStartAt();
                LocalDateTime endAt = startAt.plusHours(1);

                ReservationRequest request = new ReservationRequest();
                request.setMeetingRoomId(roomId);
                request.setStartAt(startAt);
                request.setEndAt(endAt);

                MeetingRoom meetingRoom = mock(MeetingRoom.class);

                when(meetingRoomRepository.findById(roomId))
                                .thenReturn(Optional.of(meetingRoom));

                when(reservationRepository
                                .existsByMeetingRoom_IdAndStatusAndStartAtLessThanAndEndAtGreaterThan(
                                                roomId, ReservationStatus.ACTIVE, endAt, startAt))
                                .thenReturn(true);

                assertThrows(
                                ReservationConflictException.class,
                                () -> reservationService.createReservation(request, EMAIL));

                verify(reservationRepository, never()).save(any(Reservation.class));
                verifyNoInteractions(reservationMapper);
        }

        @Test
        void createReservationChecksOnlyActiveReservations() {
                Long roomId = 1L;
                LocalDateTime startAt = validStartAt();
                LocalDateTime endAt = startAt.plusHours(1);

                ReservationRequest request = new ReservationRequest();
                request.setMeetingRoomId(roomId);
                request.setStartAt(startAt);
                request.setEndAt(endAt);

                MeetingRoom meetingRoom = mock(MeetingRoom.class);
                ReservationResponse response = new ReservationResponse();

                when(meetingRoomRepository.findById(roomId))
                                .thenReturn(Optional.of(meetingRoom));

                AppUser owner = new AppUser(EMAIL, "hashed-password");

                when(appUserRepository.findByEmail(EMAIL))
                                .thenReturn(Optional.of(owner));

                when(reservationRepository
                                .existsByMeetingRoom_IdAndStatusAndStartAtLessThanAndEndAtGreaterThan(
                                                roomId, ReservationStatus.ACTIVE, endAt, startAt))
                                .thenReturn(false);

                when(reservationRepository.save(any(Reservation.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                when(reservationMapper.toResponse(any(Reservation.class)))
                                .thenReturn(response);

                ReservationResponse result = reservationService.createReservation(request, EMAIL);

                assertSame(response, result);

                verify(reservationRepository)
                                .existsByMeetingRoom_IdAndStatusAndStartAtLessThanAndEndAtGreaterThan(
                                                roomId, ReservationStatus.ACTIVE, endAt, startAt);

                verify(reservationRepository).save(any(Reservation.class));
        }

        private ReservationRequest requestFor(
                        LocalDateTime startAt, LocalDateTime endAt) {

                ReservationRequest request = new ReservationRequest();
                request.setMeetingRoomId(1L);
                request.setStartAt(startAt);
                request.setEndAt(endAt);
                return request;
        }

        private void assertInvalidSlot(
                        LocalDateTime startAt, LocalDateTime endAt) {

                ResponseStatusException exception = assertThrows(
                                ResponseStatusException.class,
                                () -> reservationService.createReservation(
                                                requestFor(startAt, endAt), EMAIL));

                assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
                verifyNoInteractions(
                                meetingRoomRepository,
                                reservationRepository,
                                appUserRepository,
                                reservationMapper);
        }

        @Test
        void createReservationRejectsSunday() {
                LocalDateTime start = validStartAt().minusDays(1);

                assertInvalidSlot(start, start.plusHours(1));
        }

        @Test
        void createReservationRejectsStartBeforeNine() {
                LocalDateTime start = validStartAt().minusHours(2);

                assertInvalidSlot(start, start.plusHours(1));
        }

        @Test
        void createReservationRejectsEndAfterNineteen() {
                LocalDateTime start = validStartAt().withHour(18);

                assertInvalidSlot(start, start.plusHours(2));
        }

        @Test
        void createReservationRejectsDifferentEndDay() {
                LocalDateTime start = validStartAt().withHour(18);

                assertInvalidSlot(start, start.plusHours(15));
        }

        @Test
        void createReservationRejectsUnalignedStart() {
                LocalDateTime start = validStartAt().plusMinutes(15);

                assertInvalidSlot(start, start.plusHours(1));
        }

        @Test
        void createReservationRejectsUnalignedEnd() {
                LocalDateTime start = validStartAt();

                assertInvalidSlot(start, start.plusMinutes(75));
        }

        @Test
        void createReservationRejectsDurationBelowOneHour() {
                LocalDateTime start = validStartAt();

                assertInvalidSlot(start, start.plusMinutes(30));
        }

        @Test
        void createReservationRejectsDurationAboveThreeHours() {
                LocalDateTime start = validStartAt();

                assertInvalidSlot(start, start.plusMinutes(210));
        }

        @Test
        void createReservationRejectsSecondActiveReservationOnSameDay() {
                LocalDateTime startAt = validStartAt();
                LocalDateTime endAt = startAt.plusHours(1);

                ReservationRequest request = requestFor(startAt, endAt);
                MeetingRoom meetingRoom = mock(MeetingRoom.class);

                when(meetingRoomRepository.findById(1L))
                                .thenReturn(Optional.of(meetingRoom));

                when(reservationRepository
                                .existsByOwner_EmailAndStatusAndStartAtGreaterThanEqualAndStartAtLessThan(
                                                EMAIL,
                                                ReservationStatus.ACTIVE,
                                                startAt.toLocalDate().atStartOfDay(),
                                                startAt.toLocalDate().plusDays(1).atStartOfDay()))
                                .thenReturn(true);

                ResponseStatusException exception = assertThrows(
                                ResponseStatusException.class,
                                () -> reservationService.createReservation(request, EMAIL));

                assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

                verify(reservationRepository, never()).save(any(Reservation.class));
                verifyNoInteractions(appUserRepository, reservationMapper);
        }

        @Test
        void createReservationAllowsBookingWhenDailyQuotaIsFree() {
                LocalDateTime startAt = validStartAt();
                LocalDateTime endAt = startAt.plusHours(1);

                ReservationRequest request = requestFor(startAt, endAt);
                MeetingRoom meetingRoom = mock(MeetingRoom.class);
                AppUser owner = new AppUser(EMAIL, "hashed-password");
                ReservationResponse response = new ReservationResponse();

                when(meetingRoomRepository.findById(1L))
                                .thenReturn(Optional.of(meetingRoom));

                when(reservationRepository
                                .existsByOwner_EmailAndStatusAndStartAtGreaterThanEqualAndStartAtLessThan(
                                                EMAIL,
                                                ReservationStatus.ACTIVE,
                                                startAt.toLocalDate().atStartOfDay(),
                                                startAt.toLocalDate().plusDays(1).atStartOfDay()))
                                .thenReturn(false);

                when(appUserRepository.findByEmail(EMAIL))
                                .thenReturn(Optional.of(owner));

                when(reservationRepository.save(any(Reservation.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                when(reservationMapper.toResponse(any(Reservation.class)))
                                .thenReturn(response);

                ReservationResponse result = reservationService.createReservation(request, EMAIL);

                assertSame(response, result);

                verify(reservationRepository)
                                .existsByOwner_EmailAndStatusAndStartAtGreaterThanEqualAndStartAtLessThan(
                                                EMAIL,
                                                ReservationStatus.ACTIVE,
                                                startAt.toLocalDate().atStartOfDay(),
                                                startAt.toLocalDate().plusDays(1).atStartOfDay());

                verify(reservationRepository).save(any(Reservation.class));
        }
}