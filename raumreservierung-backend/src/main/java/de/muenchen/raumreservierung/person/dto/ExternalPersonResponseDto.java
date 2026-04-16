package de.muenchen.raumreservierung.person.dto;

import de.muenchen.raumreservierung.person.PersonTitle;
import de.muenchen.raumreservierung.person.PersonType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ExternalPersonResponseDto(
        UUID id,
        PersonTitle title,
        String firstName,
        String lastName,
        @NotNull String email,
        String telefonNumber,
        @NotNull PersonType type,
        String company,
        String streetAddress,
        String postalCodeCity,
        String note) implements PersonResponseDto {
}
