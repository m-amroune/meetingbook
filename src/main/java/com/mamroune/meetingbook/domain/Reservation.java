package com.mamroune.meetingbook.domain;

import java.time.LocalDateTime;

public class Reservation {

    private Long id;
    private MeetingRoom meetingRoom;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private ReservationStatus status;

    public Reservation(MeetingRoom meetingRoom, LocalDateTime startAt, LocalDateTime endAt) {
        this.meetingRoom = meetingRoom;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = ReservationStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public MeetingRoom getMeetingRoom() {
        return meetingRoom;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public ReservationStatus getStatus() {
    return status;
}
}
