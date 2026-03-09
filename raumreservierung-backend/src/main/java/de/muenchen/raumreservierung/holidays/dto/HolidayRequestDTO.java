package de.muenchen.raumreservierung.holidays.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record HolidayRequestDTO(@NotNull @Size(min = 2, max = 30) String name, @NotNull LocalDateTime startDate, @NotNull LocalDateTime endDate) {
}
