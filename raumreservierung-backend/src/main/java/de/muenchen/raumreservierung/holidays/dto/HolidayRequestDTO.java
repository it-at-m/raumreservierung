package de.muenchen.raumreservierung.holidays.dto;

import java.time.LocalDate;
import java.util.UUID;

public record HolidayRequestDTO(String name, LocalDate startDate, LocalDate endDate, UUID id) {
    public HolidayRequestDTO(String name, LocalDate startDate, LocalDate endDate) {
        this(name, startDate, endDate, null);
    }
}
