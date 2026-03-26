package de.muenchen.raumreservierung.room;

import de.muenchen.raumreservierung.common.BaseEntity;
import de.muenchen.raumreservierung.equipment.Equipment;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.io.Serial;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

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
    private List<RoomSeatingCapacity> roomSeatingCapacities;

    @ManyToMany
    private Set<Equipment> equipment;

    public void updateFrom(final Room room) {
        this.name = room.getName();
        this.information = room.getInformation();
        this.number = room.getNumber();
        this.address = room.getAddress();
        this.capacity = room.getCapacity();
        this.isActive = room.getIsActive();
        this.area = room.getArea();
        this.note = room.getNote();
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
