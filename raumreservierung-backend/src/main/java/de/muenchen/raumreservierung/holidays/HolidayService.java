package de.muenchen.raumreservierung.holidays;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.raumreservierung.common.NotFoundException;
import java.util.List;
import java.util.UUID;

import de.muenchen.raumreservierung.holidays.dto.HolidayRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HolidayService {
    private final HolidayRepository holidayRepository;

    public Holiday getHoliday(final UUID holidayId) {
        log.info("Get TheEntity with ID {}", holidayId);
        return getHolidayOrThrowException(holidayId);
    }

    private Holiday getHolidayOrThrowException(final UUID holidayId) {
        return holidayRepository
                .findById(holidayId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, holidayId)));
    }

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
