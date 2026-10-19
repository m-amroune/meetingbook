package com.mamroune.meetingbook.domain;

import java.util.Set;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "meeting_rooms")
public class MeetingRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    private int capacity;
    @ElementCollection
    @CollectionTable(name = "meeting_room_equipment", joinColumns = @JoinColumn(name = "meeting_room_id"))
    @Column(name = "equipment")
    private Set<String> equipment;
    @Enumerated(EnumType.STRING)
    private MeetingRoomStatus status;

   /**
 * Required by JPA to instantiate the entity.
 */
protected MeetingRoom() {
}

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
