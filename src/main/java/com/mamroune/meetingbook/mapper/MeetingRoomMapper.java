package com.mamroune.meetingbook.mapper;

import org.mapstruct.Mapper;

import com.mamroune.meetingbook.domain.MeetingRoom;
import com.mamroune.meetingbook.dto.MeetingRoomRequest;
import com.mamroune.meetingbook.dto.MeetingRoomResponse;

import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MeetingRoomMapper {

    MeetingRoom toEntity(MeetingRoomRequest request);

    MeetingRoomResponse toResponse(MeetingRoom meetingRoom);

    void updateEntity(MeetingRoomRequest request, @MappingTarget MeetingRoom meetingRoom);
}