package de.muenchen.raumreservierung.person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.muenchen.raumreservierung.person.domain.ExternalPerson;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private ExternalPersonRepository externalPersonRepository;

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
}
