package de.muenchen.raumreservierung.room;

import de.muenchen.raumreservierung.common.BaseEntity;
import de.muenchen.raumreservierung.seating.SeatingType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = false)
public class RoomSeatingCapacity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @OneToOne(fetch = FetchType.LAZY)
    private SeatingType seatingType;

    @Column(nullable = false)
    private int capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

}
