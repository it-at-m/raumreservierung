package de.muenchen.raumreservierung.person.dto;

import de.muenchen.raumreservierung.person.PersonTitle;
import de.muenchen.raumreservierung.person.PersonType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record InternalPersonResponseDto(
        UUID id,
        PersonTitle title,
        @NotNull String firstName,
        @NotNull String lastName,
        @NotNull String email,
        String telefonNumber,
        @NotNull PersonType type,
        @NotNull String organisationId,
        @NotNull String organisationUnit,
        @NotNull String roleFunction) implements PersonResponseDto {
}
