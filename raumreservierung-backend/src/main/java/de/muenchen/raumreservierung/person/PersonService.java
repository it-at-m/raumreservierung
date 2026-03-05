package de.muenchen.raumreservierung.person;

import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.person.dto.PersonFilterDto;
import de.muenchen.raumreservierung.security.Authorities;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public Person findById(final UUID personId) {
        return getPersonOrThrowException(personId);
    }

    @PreAuthorize(Authorities.USERS_MANAGE)
    public Page<Person> getPersonsByPageableAndFilter(final Pageable pageable, final PersonFilterDto personFilterDto) {
        return personRepository.findAll(pageable);
    }

    @Transactional
    @PreAuthorize(Authorities.USERS_MANAGE)
    public Person createPerson(final Person person) {
        return personRepository.save(person);
    }

    @Transactional
    @PreAuthorize(Authorities.USERS_MANAGE)
    public Person updatePerson(final UUID personId, final Person person) {
        Person foundPerson = getPersonOrThrowException(personId);

        if (!(person instanceof InternalPerson && foundPerson instanceof InternalPerson)) {
            throw new NotImplementedException("Changing a persons type is not yet implemented");
        }

        foundPerson.updateFrom(person);
        return personRepository.save(foundPerson);
    }

    @Transactional
    @PreAuthorize(Authorities.USERS_MANAGE)
    public void deletePerson(final UUID personId) {
        personRepository.deleteById(personId);
    }

    private Person getPersonOrThrowException(final UUID personId) {
        return personRepository
                .findById(personId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, personId)));
    }
}
