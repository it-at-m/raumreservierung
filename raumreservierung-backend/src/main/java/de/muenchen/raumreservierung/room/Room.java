package de.muenchen.raumreservierung.room;

import de.muenchen.raumreservierung.common.BaseEntity;
import de.muenchen.raumreservierung.equipment.Equipment;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@SuppressWarnings("PMD.ShortClassName")
public class Room extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String number;

    @Column
    private String address;

    @Column
    private int capacity;

    @Column(length = 1000)
    private String information;

    @Column(length = 1000)
    private String note;

    @Column(nullable = false)
    private Boolean isActive;

    @Column
    private int area;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "room")
    private List<RoomSeatingCapacity> roomSeatingCapacities = new ArrayList<>();

    @ManyToMany
    private Set<Equipment> equipment = new HashSet<>();

    public void updateFrom(final Room roomChanges) {
        this.name = roomChanges.getName();
        this.information = roomChanges.getInformation();
        this.number = roomChanges.getNumber();
        this.address = roomChanges.getAddress();
        this.capacity = roomChanges.getCapacity();
        this.isActive = roomChanges.getIsActive();
        this.area = roomChanges.getArea();
        this.note = roomChanges.getNote();


        this.equipment.clear();
        if (roomChanges.getEquipment() != null) {
            this.equipment.addAll(roomChanges.getEquipment());
        }

        this.roomSeatingCapacities.clear();
        if (roomChanges.getRoomSeatingCapacities() != null) {
            this.roomSeatingCapacities.addAll(roomChanges.getRoomSeatingCapacities());
            this.roomSeatingCapacities.forEach(csc -> csc.setRoom(this));
        }
    }

    public void updateRoomSeatingCapacityFrom(final List<RoomSeatingCapacity> roomSeatingCapacities) {
        if (this.roomSeatingCapacities != null) {
            this.roomSeatingCapacities.clear();
            this.roomSeatingCapacities.addAll(roomSeatingCapacities);
        } else {
            this.roomSeatingCapacities = roomSeatingCapacities;
        }
    }
}
