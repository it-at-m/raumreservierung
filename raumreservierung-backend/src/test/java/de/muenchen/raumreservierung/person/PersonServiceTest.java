package de.muenchen.raumreservierung.person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.muenchen.raumreservierung.common.ConflictException;
import de.muenchen.raumreservierung.person.domain.ExternalPerson;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private ExternalPersonRepository externalPersonRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PersonService personService;

    @Captor
    private ArgumentCaptor<LocalDate> dateCaptor;

    @Test
    void shouldDeleteInactiveExternalPersonsAndReturnCount() {
        int monthsOld = 6;

        ExternalPerson person1 = new ExternalPerson();
        ExternalPerson person2 = new ExternalPerson();
        List<ExternalPerson> inactivePersons = List.of(person1, person2);

        when(externalPersonRepository.findByLastModifiedBefore(any(LocalDate.class)))
                .thenReturn(inactivePersons);

        int deletedCount = personService.deleteExternalPersonsOlderThan(monthsOld);

        assertThat(deletedCount).isEqualTo(2);

        verify(externalPersonRepository).findByLastModifiedBefore(dateCaptor.capture());
        LocalDate capturedDate = dateCaptor.getValue();
        LocalDate expectedDate = LocalDate.now().minusMonths(monthsOld);
        assertThat(capturedDate).isEqualTo(expectedDate);

        verify(externalPersonRepository, times(1)).deleteAllInBatch(inactivePersons);
    }

    @Test
    void shouldDoNothingWhenNoInactiveExternalPersonsFound() {
        int monthsOld = 12;

        when(externalPersonRepository.findByLastModifiedBefore(any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        int deletedCount = personService.deleteExternalPersonsOlderThan(monthsOld);

        assertThat(deletedCount).isZero();

        verify(externalPersonRepository, never()).deleteAllInBatch(any());
    }

    @Test
    void deletesPersonAndPublishesEvent_whenPersonIsExternal() {
        UUID personId = UUID.randomUUID();

        ExternalPerson externalPerson = new ExternalPerson();
        externalPerson.setId(personId);

        when(personRepository.findById(personId)).thenReturn(Optional.of(externalPerson));

        personService.deletePerson(personId);

        verify(eventPublisher).publishEvent(new PersonDeleteEvent(personId));
        verify(personRepository).deleteById(personId);
    }

    @Test
    void throwsException_andDoesNotDeleteOrPublish_whenPersonIsInternal() {
        UUID personId = UUID.randomUUID();

        InternalPerson internalPerson = new InternalPerson();
        internalPerson.setId(personId);

        when(personRepository.findById(personId)).thenReturn(Optional.of(internalPerson));

        assertThatThrownBy(() -> personService.deletePerson(personId))
                .isInstanceOf(ConflictException.class);

        verify(eventPublisher, never()).publishEvent(any());
        verify(personRepository, never()).deleteById(any());
    }
}
