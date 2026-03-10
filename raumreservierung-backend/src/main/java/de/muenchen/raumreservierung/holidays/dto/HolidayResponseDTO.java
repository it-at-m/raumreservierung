package de.muenchen.raumreservierung.holidays.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public record HolidayResponseDTO(@NotNull @Size(min = 2, max = 100) String name, @NotNull LocalDate startDate, @NotNull LocalDate endDate, UUID id) {
}
