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

@ExtendWith(MockitoExtension.class)

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private MeetingRoomRepository meetingRoomRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void getAllReservationsReturnsMappedReservations() {
        Reservation reservation = mock(Reservation.class);
        ReservationResponse response = new ReservationResponse();

        when(reservationRepository.findAll()).thenReturn(List.of(reservation));
        when(reservationMapper.toResponse(reservation)).thenReturn(response);

        List<ReservationResponse> result = reservationService.getAllReservations();

        assertEquals(1, result.size());
        assertSame(response, result.get(0));

        verify(reservationRepository).findAll();
        verify(reservationMapper).toResponse(reservation);
    }

    @Test
    void getReservationByIdReturnsMappedReservation() {
        Long id = 1L;
        Reservation reservation = mock(Reservation.class);
        ReservationResponse response = new ReservationResponse();

        when(reservationRepository.findById(id))
                .thenReturn(Optional.of(reservation));
        when(reservationMapper.toResponse(reservation))
                .thenReturn(response);

        ReservationResponse result = reservationService.getReservationById(id);

        assertSame(response, result);

        verify(reservationRepository).findById(id);
        verify(reservationMapper).toResponse(reservation);
    }

    @Test
    void getReservationByIdThrowsWhenReservationNotFound() {
        Long id = 999L;

        when(reservationRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ReservationNotFoundException.class,
                () -> reservationService.getReservationById(id));

        verify(reservationRepository).findById(id);
        verifyNoInteractions(reservationMapper);
    }

    @Test
    void createReservationSavesAndReturnsMappedReservation() {
        Long roomId = 1L;
        LocalDateTime startAt = LocalDateTime.now().plusDays(1);
        LocalDateTime endAt = startAt.plusHours(1);

        ReservationRequest request = new ReservationRequest();
        request.setMeetingRoomId(roomId);
        request.setStartAt(startAt);
        request.setEndAt(endAt);

        MeetingRoom meetingRoom = mock(MeetingRoom.class);
        ReservationResponse response = new ReservationResponse();

        when(meetingRoomRepository.findById(roomId))
                .thenReturn(Optional.of(meetingRoom));

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(reservationMapper.toResponse(any(Reservation.class)))
                .thenReturn(response);

        ReservationResponse result = reservationService.createReservation(request);

        assertSame(response, result);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);

        verify(reservationRepository).save(captor.capture());

        Reservation savedReservation = captor.getValue();

        assertSame(meetingRoom, savedReservation.getMeetingRoom());
        assertEquals(startAt, savedReservation.getStartAt());
        assertEquals(endAt, savedReservation.getEndAt());

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
                () -> reservationService.createReservation(request));

        verifyNoInteractions(
                meetingRoomRepository,
                reservationRepository,
                reservationMapper);
    }

    @Test
    void createReservationThrowsWhenMeetingRoomNotFound() {
        Long roomId = 999L;
        LocalDateTime startAt = LocalDateTime.now().plusDays(1);

        ReservationRequest request = new ReservationRequest();
        request.setMeetingRoomId(roomId);
        request.setStartAt(startAt);
        request.setEndAt(startAt.plusHours(1));

        when(meetingRoomRepository.findById(roomId))
                .thenReturn(Optional.empty());

        assertThrows(
                MeetingRoomNotFoundException.class,
                () -> reservationService.createReservation(request));

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

        when(reservationRepository.findById(id))
                .thenReturn(Optional.of(reservation));

        when(reservationRepository.save(reservation))
                .thenReturn(reservation);

        when(reservationMapper.toResponse(reservation))
                .thenReturn(response);

        assertEquals(ReservationStatus.ACTIVE, reservation.getStatus());

        ReservationResponse result = reservationService.cancelReservation(id);

        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        assertSame(response, result);

        verify(reservationRepository).findById(id);
        verify(reservationRepository).save(reservation);
        verify(reservationMapper).toResponse(reservation);
    }

    @Test
    void cancelReservationThrowsWhenReservationNotFound() {
        Long id = 999L;

        when(reservationRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ReservationNotFoundException.class,
                () -> reservationService.cancelReservation(id));

        verify(reservationRepository).findById(id);
        verifyNoMoreInteractions(reservationRepository);
        verifyNoInteractions(meetingRoomRepository, reservationMapper);
    }
}