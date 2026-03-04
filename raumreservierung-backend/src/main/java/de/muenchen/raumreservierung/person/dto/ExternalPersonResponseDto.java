package de.muenchen.raumreservierung.person.dto;

import jakarta.validation.constraints.NotNull;

public record ExternalPersonRequestDto(
        @NotNull String name,
        @NotNull String email,
        String telefonNumber,
        String company,
        String streetAddress,
        String postalCodeCity) implements PersonRequestDto {
}
