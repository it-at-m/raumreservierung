package de.muenchen.raumreservierung.person.dto;

import de.muenchen.raumreservierung.person.PersonType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InternalPersonResponseDto(
        UUID id,
        @NotNull String name,
        @NotNull String email,
        String telefonNumber,
        @NotNull PersonType type,
        @NotNull String organisationId,
        @NotNull String organisationUnit,
        @NotNull String function) implements PersonResponseDto {
}
