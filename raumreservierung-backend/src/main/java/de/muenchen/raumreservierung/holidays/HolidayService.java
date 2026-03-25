package de.muenchen.raumreservierung.holidays;

import de.muenchen.raumreservierung.common.BadRequestException;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.holidays.dto.HolidayFilterDto;
import de.muenchen.raumreservierung.security.Authorities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_START_DATE_AFTER_END_DATE;

@Service
@Slf4j
@RequiredArgsConstructor
public class HolidayService {
    private final HolidayRepository holidayRepository;

    public List<Holiday> getHolidays(final HolidayFilterDto holidayFilterDto) {
        LocalDate afterStartDate = LocalDate.ofYearDay(holidayFilterDto.year(), 1);
        LocalDate beforeStartDate = LocalDate.ofYearDay(holidayFilterDto.year() + 1, 1);

        final List<Holiday> holidays = holidayRepository.findAllByStartDateBetween(afterStartDate, beforeStartDate);
        log.debug("Getting holidays - year: {}", holidayFilterDto.year());
        return holidays;
    }

    @PreAuthorize(Authorities.HOLIDAYS_MANAGE)
    public Holiday createHoliday(final Holiday holiday) {
        startDateBeforeEndDateElseThrowException(holiday);
        log.debug("Creating holiday {}", holiday);
        return holidayRepository.save(holiday);
    }

    @PreAuthorize(Authorities.HOLIDAYS_MANAGE)
    public Holiday updateHoliday(final Holiday holiday, final UUID holidayId) {
        startDateBeforeEndDateElseThrowException(holiday);
        final Holiday foundHoliday = getEntityOrThrowException(holidayId);
        foundHoliday.updateHoliday(holiday);
        log.debug("Updating holiday {}", foundHoliday);
        return holidayRepository.save(foundHoliday);
    }

    @PreAuthorize(Authorities.HOLIDAYS_MANAGE)
    public void deleteHoliday(final UUID holidayId) {
        log.debug("Deleting holiday {}", holidayId);
        holidayRepository.deleteById(holidayId);
    }

    private Holiday getEntityOrThrowException(final UUID holidayId) {
        return holidayRepository
                .findById(holidayId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, holidayId)));
    }

    private void startDateBeforeEndDateElseThrowException(final Holiday holiday) {
        if (holiday.getStartDate().isAfter(holiday.getEndDate())) {
            throw new BadRequestException(MSG_START_DATE_AFTER_END_DATE);
        }
    }
}
