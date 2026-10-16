package com.mamroune.meetingbook.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mamroune.meetingbook.service.MeetingRoomService;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

import com.mamroune.meetingbook.dto.MeetingRoomResponse;

import org.springframework.web.bind.annotation.PathVariable;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mamroune.meetingbook.dto.MeetingRoomRequest;

import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.DeleteMapping;



@RestController
@RequestMapping("/api/meeting-rooms")
public class MeetingRoomController {

    private final MeetingRoomService meetingRoomService;

    public MeetingRoomController(MeetingRoomService meetingRoomService) {
        this.meetingRoomService = meetingRoomService;
    }

    @GetMapping
    public List<MeetingRoomResponse> getAllMeetingRooms() {
        return meetingRoomService.getAllMeetingRooms();
    }

    @GetMapping("/{id}")
    public MeetingRoomResponse getMeetingRoomById(@PathVariable Long id) {
        return meetingRoomService.getMeetingRoomById(id);
    }
    @PostMapping
@ResponseStatus(HttpStatus.CREATED)
public MeetingRoomResponse createMeetingRoom(
    @Valid @RequestBody MeetingRoomRequest request
) {
    return meetingRoomService.createMeetingRoom(request);
}

@PutMapping("/{id}")
public MeetingRoomResponse updateMeetingRoom(
    @PathVariable Long id,
    @Valid @RequestBody MeetingRoomRequest request
) {
    return meetingRoomService.updateMeetingRoom(id, request);
}
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void deleteMeetingRoom(@PathVariable Long id) {
    meetingRoomService.deleteMeetingRoom(id);
}

    
}
