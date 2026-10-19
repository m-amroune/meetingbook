package com.mamroune.meetingbook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ReservationConflictException extends RuntimeException {

    public ReservationConflictException() {
        super("The meeting room is already reserved for this period");
    }
}
