package com.mamroune.meetingbook.dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class MeetingRoomRequest {
    
    @NotBlank
@Size(max = 100)
private String name;

@NotBlank
@Size(max = 100)
private String location;

@Positive
private int capacity;

@NotNull
private Set<@NotBlank @Size(max = 100) String> equipment;

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
}
