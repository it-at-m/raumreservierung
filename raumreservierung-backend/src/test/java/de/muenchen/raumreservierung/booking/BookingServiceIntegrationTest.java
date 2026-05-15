package de.muenchen.raumreservierung.booking;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import de.muenchen.raumreservierung.appointment.AppointmentService;
import de.muenchen.raumreservierung.common.UnauthorizedActionException;
import de.muenchen.raumreservierung.configuration.security.SecurityConfiguration;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.person.domain.Person;
import de.muenchen.raumreservierung.security.Roles;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
        classes = {
                BookingService.class,
                SecurityConfiguration.class
        }
)
public class BookingServiceIntegrationTest {
    @Autowired
    private BookingService bookingService;
    @MockitoBean
    private BookingRepository bookingRepository;
    @MockitoBean
    private JwtDecoder jwtDecoder;
    @MockitoBean
    private EntityManager entityManager;
    @MockitoBean
    private AppointmentService appointmentService;

    @Test
    @WithMockJwt(email = "anwender@anwender.de", authorities = { Roles.ANWENDER })
    void getUserEmail_ReturnsCorrectEmail() {
        String email = bookingService.getUserEmail();
        assertThat(email).isEqualTo("anwender@anwender.de");
    }

    @Test
    @WithMockUser
    void getUserEmail_ReturnsNull_WhenPrincipalIsNotJwt() {
        String email = bookingService.getUserEmail();
        assertThat(email).isNull();
    }

    @Test
    void getUserEmail_ReturnsNull_WhenNoAuthenticationExists() {
        String email = bookingService.getUserEmail();
        assertThat(email).isNull();
    }

    @Test
    @WithMockJwt(email = "anwender@anwender.de", authorities = { Roles.ANWENDER })
    void lacksAuthority_ReturnsTrue_IfRoleMissing() {
        boolean lacks = bookingService.lacksAuthority(Roles.LESEBERECHTIGT);
        assertThat(lacks).isTrue();
    }

    @Test
    @WithMockJwt(email = "raumadmin@raumadmin.de", authorities = { Roles.RAUM_ADMIN })
    void lacksAuthority_ShouldReturnFalse_WhenUserHasAdminRole() {
        boolean lacks = bookingService.lacksAuthority(Roles.RAUM_BUCHUNG);
        assertFalse(lacks);
    }

    @Test
    @WithMockJwt(email = "raumadmin@raumadmin.de", authorities = {})
    // User hat absolut keine Rollen
    void lacksAuthority_ReturnsTrue_WhenUserHasNoAuthoritiesAtAll() {
        boolean lacks = bookingService.lacksAuthority(Roles.ANWENDER);
        assertThat(lacks).isTrue();
    }

    @Test
    void lacksAuthority_ReturnsTrue_WhenContextIsEmpty() {
        boolean lacks = bookingService.lacksAuthority(Roles.ANWENDER);
        assertThat(lacks).isTrue();
    }

    @Test
    @WithMockJwt(email = "raumadmin@raumadmin.de", authorities = { Roles.RAUM_ADMIN })
    void validateBookingAccessOrThrowException_ShouldAllowAuthority_WhenAdmin() {
        Booking booking = new Booking();
        Person person = new InternalPerson();
        person.setEmail("terminorganisator@terminorganisator.de");
        booking.setContactPerson(person);

        assertDoesNotThrow(() -> bookingService.validateBookingAuthority(booking, Roles.TERMIN_ORGANISATOR));
    }

    @Test
    @WithMockJwt(email = "anwender@anwender.de", authorities = { Roles.ANWENDER })
    void validateBookingAccessOrThrowException_ShouldAllowAuthority_WhenEmailMatches() {
        Booking booking = new Booking();
        Person person = new InternalPerson();
        person.setEmail("anwender@anwender.de");
        booking.setContactPerson(person);

        assertDoesNotThrow(() -> bookingService.validateBookingAuthority(booking, Roles.RAUM_BUCHUNG));
    }

    @Test
    @WithMockJwt(email = "wrong@user.de", authorities = { Roles.ANWENDER })
    void validateBookingAuthority_Throws_WhenEmailMismatchesAndNotAdmin() {
        Booking booking = new Booking();
        Person owner = new InternalPerson();
        owner.setEmail("real@user.de");
        booking.setContactPerson(owner);

        assertThrows(UnauthorizedActionException.class, () -> bookingService.validateBookingAuthority(booking, Roles.RAUM_ADMIN));
    }

    @Test
    @WithMockJwt(email = "anwender@anwender.de", authorities = { Roles.ANWENDER })
    void hasBookingAccessOrThrowException_Throws_WhenBookingEnsureNoOwner() {
        Booking booking = new Booking();
        booking.setContactPerson(null);

        assertThrows(RuntimeException.class, () -> bookingService.validateBookingAuthority(booking, Roles.RAUM_ADMIN));
    }
}
