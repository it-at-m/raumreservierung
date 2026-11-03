package de.muenchen.raumreservierung.person.dto;

import de.muenchen.raumreservierung.person.domain.PersonType;

public record PersonFilterDto(
        String searchName,
        PersonType personType) {
}
