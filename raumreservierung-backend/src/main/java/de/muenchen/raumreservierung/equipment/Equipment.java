package de.muenchen.raumreservierung.equipment;

import de.muenchen.raumreservierung.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;


@Entity
@Setter
@Getter
public class Equipment extends BaseEntity {

    @Column(length = 100, nullable = false)
    String name;

    @Column
    String description;

}
