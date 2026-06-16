package de.muenchen.raumreservierung.person.dto;

import de.muenchen.raumreservierung.person.domain.PersonTitle;
import de.muenchen.raumreservierung.person.domain.PersonType;
import java.util.UUID;

public record MinimalPersonResponseDto(
        UUID id,
        PersonTitle title,
        String firstName,
        String lastName,
        String telefonNumber,
        String email,
        PersonType type) implements PersonResponseDto {
}
