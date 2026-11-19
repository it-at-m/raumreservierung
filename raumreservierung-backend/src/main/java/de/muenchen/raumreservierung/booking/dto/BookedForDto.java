package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.person.domain.PersonType;

public record BookedForDto (
   // This id is "multiformat".
   // Either it is a UUID when referring to an external person
   // Or it is a Ldap-Object-ID when referring to an internal person
   String id,
   PersonType personType
){}
