package com.mamroune.meetingbook.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

public class MeetingRoomTest {

    @Test
    void shouldCreateActiveMeetingRoom() {
        MeetingRoom room = new MeetingRoom(
            "Victor Hugo",
            "2e étage",
            8,
            Set.of("Écran", "Visioconférence")
        );

        assertEquals("Victor Hugo", room.getName());
        assertEquals(8, room.getCapacity());
        assertEquals(MeetingRoomStatus.ACTIVE, room.getStatus());
    }
}
