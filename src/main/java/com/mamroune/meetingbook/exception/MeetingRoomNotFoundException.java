package com.mamroune.meetingbook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MeetingRoomNotFoundException extends RuntimeException {

    public MeetingRoomNotFoundException(Long id) {
        super("Meeting room not found with id: " + id);
    }
}