package de.muenchen.raumreservierung.room;

import de.muenchen.raumreservierung.common.BaseEntity;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.seating.SeatingType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import java.io.Serial;
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

    @Column(length = 10)
    private String number;

    @Column
    private String address;

    @Column
    private int capacity;

    @Column(length = 1000)
    private String information;

    @Column(length = 1000)
    private String note;

    @Column
    private Boolean availability;

    @Column
    private int area;

    @ManyToMany
    private Set<SeatingType> seatingType;

    @ManyToMany
    private Set<Equipment> equipment;
}
