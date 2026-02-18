package de.muenchen.raumreservierung.holidays;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

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
@Table(name = "holidays")
public class Holiday {

    @Id
    @Column(name = "holiday_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "holiday_name", nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

}
