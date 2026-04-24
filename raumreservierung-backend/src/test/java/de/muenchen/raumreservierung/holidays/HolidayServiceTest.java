package de.muenchen.raumreservierung.holidays;

import static org.mockito.Mockito.when;

import de.muenchen.raumreservierung.holidays.dto.HolidayFilterDto;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HolidayServiceTest {

    @Mock
    private HolidayRepository holidayRepository;

    @InjectMocks
    private HolidayService holidayService;

    @Test
    void getHolidays_shouldReturnAllHolidaysInYear() {
        Holiday publicHoliday = new Holiday();
        publicHoliday.setName("Public Holiday");
        publicHoliday.setStartDate(LocalDate.of(2026, 1, 1));
        publicHoliday.setEndDate(LocalDate.of(2026, 1, 1));
        Holiday schoolHoliday = new Holiday();
        schoolHoliday.setName("School Holiday");
        schoolHoliday.setStartDate(LocalDate.of(2026, 1, 1));
        schoolHoliday.setEndDate(LocalDate.of(2026, 2, 22));

        final LocalDate afterStartDate = LocalDate.ofYearDay(1, 1);
        final LocalDate beforeStartDate = LocalDate.ofYearDay(2, 1);

        when(holidayRepository.findAllByStartDateBetween(afterStartDate, beforeStartDate)).thenReturn(List.of(publicHoliday, schoolHoliday));

        List<Holiday> result = holidayService.getHolidays(new HolidayFilterDto(1));

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(publicHoliday, result.getFirst());
    }

}
