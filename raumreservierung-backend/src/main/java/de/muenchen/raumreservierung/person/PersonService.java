package de.muenchen.raumreservierung.person;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.person.domain.ExternalPerson;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.person.domain.Person;
import de.muenchen.raumreservierung.person.domain.PersonType;
import de.muenchen.raumreservierung.person.dto.PersonFilterDto;
import de.muenchen.raumreservierung.security.Authorities;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final InternalPersonRepository internalPersonRepository;
    private final ExternalPersonRepository externalPersonRepository;

    // TODO consider returning less information here
    public Person findById(final UUID personId) {
        return getPersonOrThrowException(personId);
    }

    @PreAuthorize(Authorities.USERS_MANAGE)
    public Page<Person> getPersonsByPageableAndFilter(final Pageable pageable, final PersonFilterDto personFilterDto) {

        // Persontype differentiation will be done without specs as this saves an inner join!
        if (personFilterDto.personType().equals(PersonType.EXTERNAL)) {

            final Specification<ExternalPerson> personSpecification = PersonSpecificationBuilder.fromFilter(personFilterDto);
            final Specification<ExternalPerson> externalPersonSpecification = ExternalPersonSpecificationBuilder.fromFilter(personFilterDto);

            return externalPersonRepository.findAll(personSpecification.or(externalPersonSpecification), pageable)
                    .map(externalPerson -> (Person) externalPerson);
        } else {

            final Specification<InternalPerson> internalPersonSpecification = PersonSpecificationBuilder.fromFilter(personFilterDto);
            return internalPersonRepository.findAll(internalPersonSpecification, pageable).map(internalPerson -> (Person) internalPerson);
        }
    }

    @Transactional
    @PreAuthorize(Authorities.USERS_MANAGE)
    public Person createPerson(final Person person) {
        if (person instanceof ExternalPerson externalPerson) {
            externalPerson.updateLastModified(null);
        }

        return personRepository.save(person);
    }

    @Transactional
    @PreAuthorize(Authorities.USERS_MANAGE)
    public Person updatePerson(final UUID personId, final Person person) {
        final Person foundPerson = getPersonOrThrowException(personId);

        final Class<?> newPersonClass = ClassUtils.getUserClass(person);
        final Class<?> foundPersonClass = ClassUtils.getUserClass(foundPerson);

        if (!newPersonClass.equals(foundPersonClass)) {
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

    /**
     * Updates the lastModified field on externalPerson to a future date or now
     *
     * @param extPersonId the externalPerson to update
     * @param futureDate the new date which should be set, which is at minimum today
     */
    @Transactional
    public void updateLastModifiedOnExternal(final UUID extPersonId, final LocalDate futureDate) {
        final Person person = getPersonOrThrowException(extPersonId);

        if (person instanceof ExternalPerson externalPerson) {

            externalPerson.updateLastModified(futureDate);

            externalPersonRepository.save(externalPerson);
        }
    }

    /**
     * Delete all externalPersons with lastModified before cutOffDate
     *
     * @param monthsOld amount of months after which external persons will be deleted
     * @return the amount of external persons deleted
     */
    @Transactional
    public int deleteExternalPersonsOlderThan(final int monthsOld) {
        final LocalDate cutOffDate = LocalDate.now().minusMonths(monthsOld);

        final List<ExternalPerson> inactiveExternalPersons = externalPersonRepository.findByLastModifiedBefore(cutOffDate);

        if (inactiveExternalPersons.isEmpty()) {
            return 0;
        }

        externalPersonRepository.deleteAllInBatch(inactiveExternalPersons);

        return inactiveExternalPersons.size();
    }
}
