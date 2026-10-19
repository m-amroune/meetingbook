package com.mamroune.meetingbook.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mamroune.meetingbook.domain.Reservation;
import com.mamroune.meetingbook.dto.ReservationResponse;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(source = "meetingRoom.id", target = "meetingRoomId")
    @Mapping(source = "meetingRoom.name", target = "meetingRoomName")
    ReservationResponse toResponse(Reservation reservation);
}
