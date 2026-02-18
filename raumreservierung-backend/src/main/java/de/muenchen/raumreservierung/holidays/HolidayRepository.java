package de.muenchen.raumreservierung.holidays;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidayRepository extends CrudRepository<Holiday, UUID> {

    @Query("SELECT h FROM Holiday h WHERE h.startDate = h.endDate")
    List<Holiday> findPublicHolidays();

    @Query("SELECT h FROM Holiday h WHERE h.startDate != h.endDate")
    List<Holiday> findSchoolHolidays();

}
