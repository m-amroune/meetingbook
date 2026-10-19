package com.mamroune.meetingbook.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mamroune.meetingbook.service.ReservationService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

import com.mamroune.meetingbook.dto.ReservationResponse;

import org.springframework.web.bind.annotation.PathVariable;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mamroune.meetingbook.dto.ReservationRequest;

import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
    this.reservationService = reservationService;
}

@GetMapping
public List<ReservationResponse> getAllReservations() {
    return reservationService.getAllReservations();
}

@GetMapping("/{id}")
public ReservationResponse getReservationById(@PathVariable Long id) {
    return reservationService.getReservationById(id);
}

@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public ReservationResponse createReservation(
        @Valid @RequestBody ReservationRequest request) {
    return reservationService.createReservation(request);
}

@PatchMapping("/{id}/cancel")
public ReservationResponse cancelReservation(@PathVariable Long id) {
    return reservationService.cancelReservation(id);
}
}