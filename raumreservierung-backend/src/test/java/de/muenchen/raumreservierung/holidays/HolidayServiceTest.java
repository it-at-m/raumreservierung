package de.muenchen.raumreservierung.holidays;

import de.muenchen.raumreservierung.holidays.dto.HolidayFilterDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HolidayServiceTest {

    @Mock
    private HolidayRepository holidayRepository;

    @InjectMocks
    private HolidayService holidayService;

    @Test
    void getHolidays_shouldReturnOnlyPublicHolidays_whenIsPublicIsTrue() {
        Holiday publicHoliday = new Holiday();
        publicHoliday.setName("Public Holiday");
        publicHoliday.setStartDate(LocalDate.of(2026, 1, 1));
        publicHoliday.setEndDate(LocalDate.of(2026, 1, 1));
        Holiday schoolHoliday = new Holiday();
        schoolHoliday.setName("School Holiday");
        schoolHoliday.setStartDate(LocalDate.of(2026, 1, 1));
        schoolHoliday.setEndDate(LocalDate.of(2026, 2, 22));

        when(holidayRepository.findAll()).thenReturn(List.of(publicHoliday, schoolHoliday));

        List<Holiday> result = holidayService.getHolidays(new HolidayFilterDto(true, 1));

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(publicHoliday, result.getFirst());
    }

    @Test
    void getHolidays_shouldReturnOnlySchoolHolidays_whenIsPublicIsFalse() {
        Holiday publicHoliday = new Holiday();
        publicHoliday.setName("Public Holiday");
        publicHoliday.setStartDate(LocalDate.of(2026, 1, 1));
        publicHoliday.setEndDate(LocalDate.of(2026, 1, 1));
        Holiday schoolHoliday = new Holiday();
        schoolHoliday.setName("School Holiday");
        schoolHoliday.setStartDate(LocalDate.of(2026, 1, 1));
        schoolHoliday.setEndDate(LocalDate.of(2026, 2, 22));

        when(holidayRepository.findAll()).thenReturn(List.of(publicHoliday, schoolHoliday));

        List<Holiday> result = holidayService.getHolidays(new HolidayFilterDto(false, 1));

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(schoolHoliday, result.getFirst());
    }
}
