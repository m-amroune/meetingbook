package com.mamroune.meetingbook.repository;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.mamroune.meetingbook.domain.MeetingRoom;
import com.mamroune.meetingbook.domain.MeetingRoomStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class MeetingRoomRepositoryTest {
    @Autowired
    private MeetingRoomRepository meetingRoomRepository;

    @Test
    void shouldPersistMeetingRoom() {
        MeetingRoom room = new MeetingRoom(
                "Victor Hugo",
                "2e étage",
                8,
                Set.of("Écran", "Visioconférence"));

        MeetingRoom savedRoom = meetingRoomRepository.save(room);

        assertNotNull(savedRoom.getId());

        MeetingRoom foundRoom = meetingRoomRepository
                .findById(savedRoom.getId())
                .orElseThrow();

        assertEquals("Victor Hugo", foundRoom.getName());
        assertEquals(8, foundRoom.getCapacity());
        assertEquals(MeetingRoomStatus.ACTIVE, foundRoom.getStatus());
    }
}