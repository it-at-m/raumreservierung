package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.person.domain.PersonType;

public record BookedForDto (
   String id,
   PersonType personType
){}
