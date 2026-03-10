package de.muenchen.raumreservierung.holidays;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_START_DATE_AFTER_END_DATE;

import de.muenchen.raumreservierung.common.BadRequestException;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.security.Authorities;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HolidayService {
    private final HolidayRepository holidayRepository;

    public List<Holiday> getHolidays(final boolean isPublic) {
        final List<Holiday> holidays = holidayRepository.findAll();
        log.debug("Getting holidays - isPublic: {}", isPublic);
        return holidays.stream().filter(h -> startDateEqualsEndDateIfPublic(isPublic, h)).toList();
    }

    /**
     * Predicate to check if dates in the specific holiday types are set correct.
     *
     * @param isPublic boolean to differentiate between desired holiday type
     * @param h holiday to check
     * @return true, if dates are set correct, i.e. start date equals end date for public holidays,
     *         start date not equal end date for school holidays,
     *         false otherwise.
     */
    private boolean startDateEqualsEndDateIfPublic(final boolean isPublic, final Holiday h) {
        final boolean startDateEqualsEndDate = h.getStartDate().isEqual(h.getEndDate());
        return isPublic == startDateEqualsEndDate;
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
