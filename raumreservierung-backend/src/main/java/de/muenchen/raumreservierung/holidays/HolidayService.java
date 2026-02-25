package de.muenchen.raumreservierung.holidays;

import java.util.List;
import java.util.UUID;

import de.muenchen.raumreservierung.common.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class HolidayService {
    private final HolidayRepository holidayRepository;

    public List<Holiday> getPublicHolidays() {
        log.info("Getting all public holidays");
        return holidayRepository.findPublicHolidays();
    }

    public List<Holiday> getSchoolHolidays() {
        log.info("Getting all school holidays");
        return holidayRepository.findSchoolHolidays();
    }

    public Holiday createHoliday(final Holiday holiday) {
        log.info("Creating holiday {}", holiday);
        return holidayRepository.save(holiday);
    }

    public Holiday updateHoliday(final Holiday holiday, final UUID holidayId) {
        final Holiday foundHoliday = getEntityOrThrowException(holidayId);
        foundHoliday.updateHoliday(holiday);
        log.info("Updating holiday {}", foundHoliday);
        return holidayRepository.save(foundHoliday);
    }

    public void deleteHoliday(final UUID holidayId) {
        log.info("Deleting holiday {}", holidayId);
        holidayRepository.deleteById(holidayId);
    }

    private Holiday getEntityOrThrowException(final UUID holidayId) {
        return holidayRepository
                .findById(holidayId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, holidayId)));
    }
}
