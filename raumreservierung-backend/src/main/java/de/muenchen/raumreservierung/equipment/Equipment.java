package de.muenchen.raumreservierung.equipment;

import de.muenchen.raumreservierung.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.io.Serial;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class Equipment extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(length = 100, nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private boolean isActive;

    public void updateFrom(final Equipment equipment) {
        this.name = equipment.getName();
        this.description = equipment.getDescription();
        this.isActive = equipment.isActive();
    }

}
