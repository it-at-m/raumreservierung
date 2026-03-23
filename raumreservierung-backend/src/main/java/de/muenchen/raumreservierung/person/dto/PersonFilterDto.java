package de.muenchen.raumreservierung.person.dto;

import de.muenchen.raumreservierung.person.PersonType;

public record PersonFilterDto(
        String searchName,
        PersonType personType) {
}
