package de.muenchen.raumreservierung.holidays.dto;

import java.time.LocalDate;

public record HolidayRequestDTO(String name, LocalDate startDate, LocalDate endDate) {
}
