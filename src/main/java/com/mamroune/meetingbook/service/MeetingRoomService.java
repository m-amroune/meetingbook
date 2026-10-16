package com.mamroune.meetingbook.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mamroune.meetingbook.domain.MeetingRoom;
import com.mamroune.meetingbook.dto.MeetingRoomRequest;
import com.mamroune.meetingbook.dto.MeetingRoomResponse;
import com.mamroune.meetingbook.exception.MeetingRoomNotFoundException;
import com.mamroune.meetingbook.mapper.MeetingRoomMapper;
import com.mamroune.meetingbook.repository.MeetingRoomRepository;

@Service
public class MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;
    private final MeetingRoomMapper meetingRoomMapper;

    public MeetingRoomService(
            MeetingRoomRepository meetingRoomRepository,
            MeetingRoomMapper meetingRoomMapper) {
        this.meetingRoomRepository = meetingRoomRepository;
        this.meetingRoomMapper = meetingRoomMapper;
    }

    public List<MeetingRoomResponse> getAllMeetingRooms() {
        return meetingRoomRepository.findAll()
                .stream()
                .map(meetingRoomMapper::toResponse)
                .toList();
    }

    public MeetingRoomResponse getMeetingRoomById(Long id) {
        MeetingRoom meetingRoom = meetingRoomRepository.findById(id)
                .orElseThrow(() -> new MeetingRoomNotFoundException(id));

        return meetingRoomMapper.toResponse(meetingRoom);
    }

    public MeetingRoomResponse createMeetingRoom(MeetingRoomRequest request) {
        MeetingRoom meetingRoom = meetingRoomMapper.toEntity(request);
        MeetingRoom savedMeetingRoom = meetingRoomRepository.save(meetingRoom);

        return meetingRoomMapper.toResponse(savedMeetingRoom);
    }

    public MeetingRoomResponse updateMeetingRoom(Long id, MeetingRoomRequest request) {
        MeetingRoom meetingRoom = meetingRoomRepository.findById(id)
                .orElseThrow(() -> new MeetingRoomNotFoundException(id));

        meetingRoomMapper.updateEntity(request, meetingRoom);

        MeetingRoom updatedMeetingRoom = meetingRoomRepository.save(meetingRoom);

        return meetingRoomMapper.toResponse(updatedMeetingRoom);
    }

    public void deleteMeetingRoom(Long id) {
        MeetingRoom meetingRoom = meetingRoomRepository.findById(id)
                .orElseThrow(() -> new MeetingRoomNotFoundException(id));

        meetingRoomRepository.delete(meetingRoom);
    }
}