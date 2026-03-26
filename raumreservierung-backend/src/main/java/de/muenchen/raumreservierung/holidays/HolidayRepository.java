package de.muenchen.raumreservierung.holidays;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, UUID> {
    List<Holiday> findAllByStartDateBetween(LocalDate startDateAfter, LocalDate startDateBefore);
}
