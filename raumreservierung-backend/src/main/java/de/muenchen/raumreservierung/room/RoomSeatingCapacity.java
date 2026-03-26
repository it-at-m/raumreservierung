package de.muenchen.raumreservierung.room;

import de.muenchen.raumreservierung.common.BaseEntity;
import de.muenchen.raumreservierung.seating.SeatingType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.io.Serial;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = false)
public class RoomSeatingCapacity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @OneToOne
    private SeatingType seatingType;

    @Column
    private int capacity;

    @ManyToOne
    private Room room;

}
