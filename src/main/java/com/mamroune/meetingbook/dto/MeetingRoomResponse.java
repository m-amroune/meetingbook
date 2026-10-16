package com.mamroune.meetingbook.dto;

import java.util.Set;

import com.mamroune.meetingbook.domain.MeetingRoomStatus;

public class MeetingRoomResponse {

    private Long id;
    private String name;
    private String location;
    private int capacity;
    private Set<String> equipment;
    private MeetingRoomStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Set<String> getEquipment() {
        return equipment;
    }

    public void setEquipment(Set<String> equipment) {
        this.equipment = equipment;
    }

    public MeetingRoomStatus getStatus() {
        return status;
    }

    public void setStatus(MeetingRoomStatus status) {
        this.status = status;
    }
}
