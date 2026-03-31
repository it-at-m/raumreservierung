package de.muenchen.raumreservierung.person.dto;

import de.muenchen.raumreservierung.person.PersonType;
import jakarta.validation.constraints.NotNull;

public record ExternalPersonRequestDto(
        String name,
        @NotNull String email,
        String telefonNumber,
        @NotNull PersonType type,
        String company,
        String streetAddress,
        String postalCodeCity) implements PersonRequestDto {
}
