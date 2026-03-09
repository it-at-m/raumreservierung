package de.muenchen.raumreservierung.holidays;

import de.muenchen.raumreservierung.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class represents a holiday.
 * The two kinds of holidays (school holidays and public holidays) are only distinguishable by their
 * duration.
 * Duration of one day: public holiday
 * Duration of more than one day: school holiday
 */
@Entity
@Getter
@Setter
@ToString
@Table
public class Holiday extends BaseEntity {

    @Column(length = 100, nullable = false)
    @Size(min = 2, max = 100)
    private String name;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    public void updateHoliday(final Holiday holiday) {
        this.name = holiday.getName();
        this.startDate = holiday.getStartDate();
        this.endDate = holiday.getEndDate();
    }

}
