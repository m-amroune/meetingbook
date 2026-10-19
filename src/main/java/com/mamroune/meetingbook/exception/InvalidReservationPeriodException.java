package com.mamroune.meetingbook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidReservationPeriodException extends RuntimeException {

    public InvalidReservationPeriodException() {
        super("Reservation end time must be after start time");
    }
}