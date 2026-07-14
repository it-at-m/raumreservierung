package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.security.Roles;
import de.muenchen.raumreservierung.security.SecurityContextService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingTransitionServiceTest {

    @Mock
    private SecurityContextService securityContextService;

    @InjectMocks
    private BookingTransitionService bookingTransitionService;

    @ParameterizedTest
    @MethodSource("provideValidTransitions")
    void isTransitionAllowed_ReturnsTrue_ForValidTransitions(BookingStatus current, BookingStatus target, String role) {
        when(securityContextService.hasAuthority(anyString())).thenAnswer(
                invocation -> invocation.getArgument(0).equals(role)
        );

        boolean isAllowed = bookingTransitionService.isTransitionAllowed(current, target);

        assertThat(isAllowed).isTrue();
    }

    private static Stream<Arguments> provideValidTransitions() {
        return Stream.of(
                Arguments.of(BookingStatus.NEW, BookingStatus.ROOM_APPROVED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.NEW, BookingStatus.CANCELED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ORGANIZER_APPROVED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ROOM_CHANGED, Roles.ANWENDER)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidTransitions")
    void isTransitionAllowed_ReturnsFalse_ForInvalidTransitions(BookingStatus current, BookingStatus target) {
        when(securityContextService.hasAuthority(anyString())).thenReturn(false);

        boolean isAllowed = bookingTransitionService.isTransitionAllowed(current, target);

        assertThat(isAllowed).isFalse();
    }

    private static Stream<Arguments> provideInvalidTransitions() {
        return Stream.of(
                Arguments.of(BookingStatus.NEW, BookingStatus.ORGANIZER_APPROVED),
                Arguments.of(BookingStatus.UNFEASIBLE, BookingStatus.ROOM_APPROVED),
                Arguments.of(BookingStatus.CANCELED, BookingStatus.NEW)
        );
    }
}
