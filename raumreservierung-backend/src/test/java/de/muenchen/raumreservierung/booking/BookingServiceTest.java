package de.muenchen.raumreservierung.booking;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import de.muenchen.raumreservierung.appointment.AppointmentService;
import de.muenchen.raumreservierung.configuration.security.SecurityConfiguration;
import de.muenchen.raumreservierung.person.PersonService;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.room.RoomService;
import de.muenchen.raumreservierung.security.Roles;
import de.muenchen.raumreservierung.security.SecurityContextService;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
        classes = {
                BookingService.class,
                SecurityConfiguration.class,
                SecurityContextService.class
        }
)
public class BookingServiceTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private SecurityContextService securityContextService;
    @MockitoBean
    private BookingRepository bookingRepository;
    @MockitoBean
    private JwtDecoder jwtDecoder;
    @MockitoBean
    private EntityManager entityManager;
    @MockitoBean
    private AppointmentService appointmentService;
    @MockitoBean
    private PersonService personService;
    @MockitoBean
    private RoomService roomService;
    @MockitoBean
    private BookingValidationService bookingValidationService;

    @Test
    @WithMockJwt(lhmObjectID = "987654", authorities = { Roles.RAUM_ADMIN })
    void validateBookingAccess_ShouldReturnTrue_WhenAdmin() {
        Booking booking = new Booking();
        InternalPerson person = new InternalPerson();
        person.setOrganisationId("12345");
        booking.setBookedBy(person);

        assertTrue(bookingService.validateBookingAuthority(booking, Roles.TERMIN_ORGANISATOR));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void validateBookingAccess_ShouldReturnTrue_WhenOIDMatches() {
        Booking booking = new Booking();
        InternalPerson person = new InternalPerson();
        person.setOrganisationId("000001");
        person.setId(UUID.fromString("12345678-abcd-ef01-2345-6789abcdef01"));
        booking.setBookedBy(person);

        when(personService.resolveInternalPersonByOrganisationIDOrThrowException(securityContextService.getCurrentOID()))
                .thenReturn(person);

        assertTrue(bookingService.validateBookingAuthority(booking, Roles.RAUM_BUCHUNG));
    }

    @Test
    @WithMockJwt(lhmObjectID = "012345", authorities = { Roles.ANWENDER })
    void validateBookingAuthority_ShouldReturnFalse_WhenOIDMismatchesAndNotAdmin() {
        Booking booking = new Booking();
        InternalPerson owner = new InternalPerson();
        owner.setOrganisationId("987654");
        owner.setId(UUID.fromString("12345678-abcd-ef01-2345-6789abcdef01"));
        booking.setBookedBy(owner);
        booking.setBookedFor(owner);

        InternalPerson currentUser = new InternalPerson();
        currentUser.setOrganisationId("012345");
        currentUser.setId(UUID.fromString("99999999-aaaa-bbbb-cccc-dddddddddddd"));

        when(personService.resolveInternalPersonByOrganisationIDOrThrowException(securityContextService.getCurrentOID()))
                .thenReturn(currentUser);

        assertFalse(bookingService.validateBookingAuthority(booking, Roles.RAUM_ADMIN));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void hasBookingAccess_ShouldThrow_WhenBookingHasNoOwner() {
        Booking booking = new Booking();
        booking.setBookedBy(null);

        assertThrows(NullPointerException.class, () -> bookingService.validateBookingAuthority(booking, Roles.RAUM_ADMIN));
    }

}
