package de.muenchen.raumreservierung.equipment;

import de.muenchen.raumreservierung.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Entity
@Setter
@Getter
public class Equipment extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(length = 100, nullable = false)
    private String name;

    @Column
    private String description;

    public void updateFrom(final Equipment equipment) {
        this.name = equipment.getName();
        this.description = equipment.getDescription();
    }

}
