package de.muenchen.raumreservierung.person.dto;

import de.muenchen.raumreservierung.person.domain.PersonTitle;
import de.muenchen.raumreservierung.person.domain.PersonType;
import jakarta.validation.constraints.NotNull;

public record ExternalPersonRequestDto(
        PersonTitle title,
        String firstName,
        String lastName,
        @NotNull String email,
        String telefonNumber,
        @NotNull PersonType type,
        String company,
        String streetAddress,
        String postalCodeCity,
        String note) implements PersonRequestDto {
}
