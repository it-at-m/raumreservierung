package de.muenchen.raumreservierung.person.dto;

import de.muenchen.raumreservierung.person.PersonType;
import jakarta.validation.constraints.NotNull;

public record InternalPersonRequestDto(
        @NotNull String name,
        @NotNull String email,
        String telefonNumber,
        @NotNull PersonType type,
        @NotNull String organisationId,
        @NotNull String organisationUnit) implements PersonRequestDto {
}
