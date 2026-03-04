package de.muenchen.raumreservierung.person.dto;

import jakarta.validation.constraints.NotNull;

public record InternalPersonRequestDto(
        @NotNull String name, @NotNull String email, String telefonNumber, @NotNull String organisationId,
        @NotNull String organisationUnit) implements PersonRequestDto {
}
