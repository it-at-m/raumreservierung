package de.muenchen.raumreservierung.holidays;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HolidayService {
    private final HolidayRepository holidayRepository;

    public List<Holiday> getPublicHolidays() {
        return holidayRepository.findPublicHolidays();
    }

    public List<Holiday> getSchoolHolidays() {
        return holidayRepository.findSchoolHolidays();
    }

    public Holiday createHoliday(final Holiday holiday) { return holidayRepository.save(holiday); }

    public Holiday updateHoliday(final Holiday holiday) { return holidayRepository.save(holiday); }

    public void deleteHoliday(final UUID holidayId) { holidayRepository.deleteById(holidayId); }
}
