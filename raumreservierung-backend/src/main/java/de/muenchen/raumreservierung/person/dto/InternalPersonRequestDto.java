package de.muenchen.raumreservierung.person.dto;

import de.muenchen.raumreservierung.person.PersonTitle;
import de.muenchen.raumreservierung.person.PersonType;
import jakarta.validation.constraints.NotNull;

public record InternalPersonRequestDto(
        PersonTitle title,
        @NotNull String firstName,
        @NotNull String lastName,
        @NotNull String email,
        String telefonNumber,
        @NotNull PersonType type,
        @NotNull String organisationId,
        @NotNull String organisationUnit) implements PersonRequestDto {
}
