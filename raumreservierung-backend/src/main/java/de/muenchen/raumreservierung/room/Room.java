package de.muenchen.raumreservierung.room;

import de.muenchen.raumreservierung.common.BaseEntity;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.person.domain.Person;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.io.Serial;
import java.util.HashSet;
import java.util.Optional;
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
    private String location;

    @Column
    private String locationDescription;

    @ManyToOne
    private Person contactPerson;

    @Column
    private int capacity;

    @Column(nullable = false)
    private boolean isActive;

    @Column
    private int area;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "room")
    private Set<RoomSeatingCapacity> roomSeatingCapacities = new HashSet<>();

    @ManyToMany
    private Set<Equipment> equipment = new HashSet<>();

    public void updateFrom(final Room roomChanges) {
        this.name = roomChanges.getName();
        this.number = roomChanges.getNumber();
        this.location = roomChanges.getLocation();
        this.capacity = roomChanges.getCapacity();
        this.isActive = roomChanges.isActive();
        this.area = roomChanges.getArea();
        this.locationDescription = roomChanges.getLocationDescription();
        this.contactPerson = roomChanges.getContactPerson();

        this.equipment.clear();
        if (roomChanges.getEquipment() != null) {
            this.equipment.addAll(roomChanges.getEquipment());
        }

        // update roomSeatingCapacities via merging new and old list
        if (roomChanges.getRoomSeatingCapacities() != null) {
            this.roomSeatingCapacities.removeIf(rsc -> roomChanges.getRoomSeatingCapacities()
                    .stream()
                    .noneMatch(incomingRsc -> incomingRsc.getSeatingType().equals(rsc.getSeatingType())));

            for (final RoomSeatingCapacity incomingRsc : roomChanges.getRoomSeatingCapacities()) {
                final Optional<RoomSeatingCapacity> opRsc = this.roomSeatingCapacities
                        .stream()
                        .filter(rsc -> incomingRsc.getSeatingType().equals(rsc.getSeatingType()))
                        .findFirst();

                if (opRsc.isPresent()) {
                    opRsc.get().setCapacity(incomingRsc.getCapacity());
                } else {
                    incomingRsc.setRoom(this);
                    this.roomSeatingCapacities.add(incomingRsc);
                }
            }
        } else {
            this.roomSeatingCapacities.clear();
        }
    }
}
