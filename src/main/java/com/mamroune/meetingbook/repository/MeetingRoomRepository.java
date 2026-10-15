package com.mamroune.meetingbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mamroune.meetingbook.domain.MeetingRoom;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {

}
