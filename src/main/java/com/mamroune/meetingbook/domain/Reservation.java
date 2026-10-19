package com.mamroune.meetingbook.domain;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "meeting_room_id", nullable = false)
    private MeetingRoom meetingRoom;
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;
    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

  /**
 * Required by JPA to instantiate the entity.
 */
protected Reservation() {
}

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

   /**
 * Marks the reservation as cancelled without deleting its record.
 */
public void cancel() {
    this.status = ReservationStatus.CANCELLED;
}
}
