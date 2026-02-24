package de.muenchen.raumreservierung.holidays.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record HolidayRequestDTO(String name, LocalDateTime startDate, LocalDateTime endDate, UUID id) {
    public HolidayRequestDTO(String name, LocalDateTime startDate, LocalDateTime endDate) {
        this(name, startDate, endDate, null);
    }
}
