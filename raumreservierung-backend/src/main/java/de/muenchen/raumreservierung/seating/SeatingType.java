package de.muenchen.raumreservierung.seating;

import de.muenchen.raumreservierung.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
@Entity
public class SeatingType extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(length = 100, nullable = false)
    private String name;

    @Column
    private String description;

    public void updateFrom(final SeatingType seatingType) {
        this.name = seatingType.getName();
        this.description = seatingType.getDescription();
    }
}
