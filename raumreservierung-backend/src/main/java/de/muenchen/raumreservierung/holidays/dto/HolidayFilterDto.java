package de.muenchen.raumreservierung.holidays.dto;

import jakarta.validation.constraints.NotNull;

public record HolidayFilterDto(
        boolean isPublic,
        @NotNull int year
) {
}
