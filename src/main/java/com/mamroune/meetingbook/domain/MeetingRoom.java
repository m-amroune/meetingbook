package com.mamroune.meetingbook.domain;

import java.util.Set;

public class MeetingRoom {

    private Long id;
    private String name;
    private String location;
    private int capacity;
    private Set<String> equipment;
    private MeetingRoomStatus status;

    public MeetingRoom(String name, String location, int capacity, Set<String> equipment) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.equipment = equipment;
        this.status = MeetingRoomStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public Set<String> getEquipment() {
        return equipment;
    }
    public MeetingRoomStatus getStatus() {
    return status;
}

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setEquipment(Set<String> equipment) {
        this.equipment = equipment;
    }
    public void setStatus(MeetingRoomStatus status) {
    this.status = status;
}
    
}

