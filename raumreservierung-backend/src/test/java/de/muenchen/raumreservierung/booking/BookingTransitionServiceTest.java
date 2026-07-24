package de.muenchen.raumreservierung.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import de.muenchen.raumreservierung.security.Roles;
import de.muenchen.raumreservierung.security.SecurityContextService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

@ExtendWith(MockitoExtension.class)
class BookingTransitionServiceTest {

    @Mock
    private SecurityContextService securityContextService;

    @InjectMocks
    private BookingTransitionService bookingTransitionService;

    @ParameterizedTest
    @MethodSource("provideValidTransitions")
    void isTransitionAllowed_ReturnsTrue_ForValidTransitions(BookingStatus current, BookingStatus target, String role) {
        final Set<String> reachable = reachableFrom(role);
        when(securityContextService.hasAuthority(anyString())).thenAnswer(
                invocation -> reachable.contains(invocation.getArgument(0)));

        boolean isAllowed = bookingTransitionService.isTransitionAllowed(current, target);

        assertThat(isAllowed).isTrue();
    }

    private static Stream<Arguments> provideValidTransitions() {
        return Stream.of(
                Arguments.of(BookingStatus.NEW, BookingStatus.ROOM_APPROVED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.NEW, BookingStatus.CANCELED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ORGANIZER_APPROVED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ROOM_CHANGED, Roles.ANWENDER),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.ORGANIZER_APPROVED, Roles.RAUM_ADMIN));
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
                Arguments.of(BookingStatus.CANCELED, BookingStatus.NEW));
    }

    @ParameterizedTest
    @MethodSource("provideRoleFilteredStates")
    void getPossibleNextStates_ReturnsOnlyStates_ForGrantedRole(String role, BookingStatus current, List<BookingStatus> expected) {
        final Set<String> reachable = reachableFrom(role);
        when(securityContextService.hasAuthority(anyString())).thenAnswer(
                invocation -> reachable.contains(invocation.getArgument(0)));

        List<BookingStatus> nextStates = bookingTransitionService.getPossibleNextStates(current);

        assertThat(nextStates).containsExactlyInAnyOrderElementsOf(expected);
    }

    private static Stream<Arguments> provideRoleFilteredStates() {
        return Stream.of(
                Arguments.of(Roles.ANWENDER, BookingStatus.NEW, List.of(BookingStatus.NEW, BookingStatus.CANCELED)),
                Arguments.of(Roles.ANWENDER, BookingStatus.ROOM_APPROVED,
                        List.of(BookingStatus.CANCELED, BookingStatus.ROOM_CHANGED, BookingStatus.ROOM_APPROVED)),
                Arguments.of(Roles.TERMIN_ORGANISATOR, BookingStatus.ROOM_APPROVED,
                        List.of(BookingStatus.ROOM_CHANGED, BookingStatus.ORGANIZER_APPROVED, BookingStatus.COORDINATION_NEEDED, BookingStatus.ROOM_APPROVED,
                                BookingStatus.CANCELED)),
                Arguments.of(Roles.RAUM_BUCHUNG, BookingStatus.ROOM_APPROVED,
                        List.of(BookingStatus.ROOM_CHANGED, BookingStatus.CANCELED, BookingStatus.ORGANIZER_APPROVED, BookingStatus.COORDINATION_NEEDED,
                                BookingStatus.ROOM_APPROVED)),
                Arguments.of(Roles.RAUM_ADMIN, BookingStatus.NEW,
                        List.of(BookingStatus.NEW, BookingStatus.ROOM_APPROVED, BookingStatus.UNFEASIBLE, BookingStatus.CANCELED)),
                Arguments.of(Roles.RAUM_ADMIN, BookingStatus.UNFEASIBLE, List.of(BookingStatus.UNFEASIBLE)),
                Arguments.of(Roles.RAUM_ADMIN, BookingStatus.CANCELED, List.of(BookingStatus.CANCELED)));
    }

    private static final RoleHierarchy ROLE_HIERARCHY = Roles.buildRoleHierarchy();

    private static Set<String> reachableFrom(final String grantedRole) {
        return ROLE_HIERARCHY.getReachableGrantedAuthorities(
                AuthorityUtils.createAuthorityList(grantedRole))
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }
}
