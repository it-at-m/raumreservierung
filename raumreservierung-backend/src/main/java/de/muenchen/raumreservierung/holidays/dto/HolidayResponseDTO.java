package de.muenchen.raumreservierung.holidays.dto;

import java.time.LocalDate;
import java.util.UUID;

public record HolidayResponseDTO(String name, LocalDate startDate, LocalDate endDate, UUID id) {
}
