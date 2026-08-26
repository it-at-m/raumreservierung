package de.muenchen.raumreservierung.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.muenchen.raumreservierung.appointment.AppointmentService;
import de.muenchen.raumreservierung.booking.dto.BookingFilterDTO;
import de.muenchen.raumreservierung.common.UnauthorizedActionException;
import de.muenchen.raumreservierung.configuration.security.SecurityConfiguration;
import de.muenchen.raumreservierung.person.PersonService;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.room.RoomService;
import de.muenchen.raumreservierung.security.Roles;
import de.muenchen.raumreservierung.security.SecurityContextService;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
        classes = {
                BookingService.class,
                BookingValidationService.class,
                BookingTransitionService.class,
                BookingPersistenceHelper.class,
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

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.TERMIN_ORGANISATOR })
    void getById_ShouldReturnBookingWithNotes_WhenAuthorizedAndOrganisator() {
        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking();
        booking.setInternalNotes("Geheime Notiz");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        InternalPerson person = new InternalPerson();
        person.setOrganisationId("12345");
        person.setId(UUID.randomUUID());
        booking.setBookedBy(person);
        booking.setBookedFor(person);

        Booking result = bookingService.getById(bookingId);

        assertNotNull(result);
        assertEquals("Geheime Notiz", result.getInternalNotes());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void getById_ShouldReturnBookingWithNullNotes_WhenAuthorizedButNotOrganisator() {
        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking();
        booking.setInternalNotes("Geheime Notiz");

        InternalPerson person = new InternalPerson();
        person.setOrganisationId("000001");
        person.setId(UUID.randomUUID());
        booking.setBookedBy(person);
        booking.setBookedFor(person);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(personService.getInternalPersonByOrganisationIDOrThrowException(securityContextService.getCurrentOID()))
                .thenReturn(person);

        Booking result = bookingService.getById(bookingId);

        assertNotNull(result);
        assertNull(result.getInternalNotes());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void getById_ShouldThrowUnauthorized_WhenUserHasNoAuthority() {
        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking();

        InternalPerson owner = new InternalPerson();
        owner.setOrganisationId("12345");
        owner.setId(UUID.randomUUID());
        booking.setBookedBy(owner);
        booking.setBookedFor(owner);

        InternalPerson stranger = new InternalPerson();
        stranger.setOrganisationId("99999");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(personService.getInternalPersonByOrganisationIDOrThrowException(securityContextService.getCurrentOID()))
                .thenReturn(stranger);

        assertThrows(UnauthorizedActionException.class, () -> bookingService.getById(bookingId));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.TERMIN_ORGANISATOR })
    void findAllWithSanitizedNotes_ShouldKeepNotes_WhenUserIsOrganisator() {
        Pageable pageable = Pageable.unpaged();
        BookingFilterDTO bookingFilterDTO = new BookingFilterDTO(null, null, null, List.of(BookingStatus.ORGANIZER_APPROVED));

        Booking booking = new Booking();
        booking.setInternalNotes("Geheime Notiz");

        Page<Booking> page = new PageImpl<>(List.of(booking));
        Specification<Booking> anySpec = ArgumentMatchers.any();
        when(bookingRepository.findAll(anySpec, any(Pageable.class))).thenReturn(page);

        Page<Booking> result = bookingService.getAllBookingsByPageableAndFilter(pageable, bookingFilterDTO);

        assertEquals(1, result.getContent().size());
        assertEquals("Geheime Notiz", result.getContent().getFirst().getInternalNotes());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.LESEBERECHTIGT })
    void findAllWithSanitizedNotes_ShouldNullNotes_WhenUserIsNotOrganisator() {
        Pageable pageable = Pageable.unpaged();
        BookingFilterDTO bookingFilterDTO = new BookingFilterDTO(null, null, null, List.of(BookingStatus.ORGANIZER_APPROVED));

        Booking booking = new Booking();
        booking.setInternalNotes("Geheime Notiz");

        Page<Booking> page = new PageImpl<>(List.of(booking));
        Specification<Booking> anySpec = ArgumentMatchers.any();
        when(bookingRepository.findAll(anySpec, any(Pageable.class))).thenReturn(page);

        Page<Booking> result = bookingService.getAllBookingsByPageableAndFilter(pageable, bookingFilterDTO);

        assertEquals(1, result.getContent().size());
        assertNull(result.getContent().getFirst().getInternalNotes());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.TERMIN_ORGANISATOR })
    void findOwnWithSanitizedNotes_ShouldKeepNotes_WhenUserIsOrganisator() {
        Pageable pageable = Pageable.unpaged();
        BookingFilterDTO bookingFilterDTO = new BookingFilterDTO(null, null, null, List.of(BookingStatus.ORGANIZER_APPROVED));

        Booking booking = new Booking();
        booking.setInternalNotes("Geheime Notiz");

        Page<Booking> page = new PageImpl<>(List.of(booking));
        Specification<Booking> anySpec = ArgumentMatchers.any();
        when(bookingRepository.findAll(anySpec, any(Pageable.class))).thenReturn(page);

        Page<Booking> result = bookingService.getOwnBookingsByPageableAndFilter(pageable, bookingFilterDTO);

        assertEquals(1, result.getContent().size());
        assertEquals("Geheime Notiz", result.getContent().getFirst().getInternalNotes());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.LESEBERECHTIGT })
    void findOwnWithSanitizedNotes_ShouldNullNotes_WhenUserIsNotOrganisator() {
        Pageable pageable = Pageable.unpaged();
        BookingFilterDTO bookingFilterDTO = new BookingFilterDTO(null, null, null, List.of(BookingStatus.ORGANIZER_APPROVED));

        Booking booking = new Booking();
        booking.setInternalNotes("Geheime Notiz");

        Page<Booking> page = new PageImpl<>(List.of(booking));
        Specification<Booking> anySpec = ArgumentMatchers.any();
        when(bookingRepository.findAll(anySpec, any(Pageable.class))).thenReturn(page);

        Page<Booking> result = bookingService.getOwnBookingsByPageableAndFilter(pageable, bookingFilterDTO);

        assertEquals(1, result.getContent().size());
        assertNull(result.getContent().getFirst().getInternalNotes());
    }
}
