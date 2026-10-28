package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.person.ExternalPersonRepository;
import de.muenchen.raumreservierung.person.PersonService;
import de.muenchen.raumreservierung.person.domain.Person;
import de.muenchen.raumreservierung.person.domain.PersonType;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookedForResolver {
    private final PersonService personService;
    private final ExternalPersonRepository externalPersonRepository;


    public Person resolvePerson(final BookedForDto bookedForDto) {
        if (bookedForDto.personType() == PersonType.INTERNAL) {
            return personService.resolveInternalPersonByOrganisationIDOrThrowException(bookedForDto.id());
        }

        return externalPersonRepository.findById(UUID.fromString(bookedForDto.id()))
                .orElseThrow(() ->
                        new IllegalArgumentException(String.format("No ExternalPerson found for UUID: %s", bookedForDto.id())));
    }
}
