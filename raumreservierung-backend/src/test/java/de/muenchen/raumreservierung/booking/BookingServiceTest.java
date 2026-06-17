package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_PARTICIPANT_COUNT_INVALID;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_ROOM_INACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_SEATINGTYPE_NOT_AVAILABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.muenchen.raumreservierung.appointment.AppointmentService;
import de.muenchen.raumreservierung.common.BadRequestException;
import de.muenchen.raumreservierung.configuration.security.SecurityConfiguration;
import de.muenchen.raumreservierung.person.PersonService;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.room.Room;
import de.muenchen.raumreservierung.room.RoomSeatingCapacity;
import de.muenchen.raumreservierung.room.RoomService;
import de.muenchen.raumreservierung.seating.SeatingType;
import de.muenchen.raumreservierung.security.Roles;
import de.muenchen.raumreservierung.security.SecurityContextService;
import jakarta.persistence.EntityManager;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
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

    private Booking baseBooking;
    private Room testRoom;
    private Room testRoomInactive;
    private Room testRoomWithSeatingCapacity;
    private SeatingType testSeatingType;

    @BeforeEach
    void setUp() {
        InternalPerson mockPerson = new InternalPerson();
        mockPerson.setOrganisationId("000001");
        mockPerson.setId(UUID.fromString("12345678-abcd-ef01-2345-6789abcdef01"));

        baseBooking = new Booking();
        baseBooking.setTitle("TEST_BOOKING");
        baseBooking.setBookedBy(mockPerson);

        testRoom = new Room();
        testRoom.setName("TEST_ROOM");
        testRoom.setNumber("TEST_NUMBER");
        testRoom.setActive(true);

        testSeatingType = new SeatingType();
        testSeatingType.setName("TEST_SEATING");
        RoomSeatingCapacity roomSeatingCapacity = new RoomSeatingCapacity();
        roomSeatingCapacity.setCapacity(10);
        roomSeatingCapacity.setSeatingType(testSeatingType);

        testRoomInactive = new Room();
        testRoomInactive.setName("TEST_ROOM_INACTIVE");
        testRoomInactive.setNumber("TEST_NUMBER_INACTIVE");
        testRoomInactive.setActive(false);
        testRoomInactive.setRoomSeatingCapacities(Set.of(roomSeatingCapacity));

        testRoomWithSeatingCapacity = new Room();
        testRoomWithSeatingCapacity.setName("TEST_ROOM_WITH_SEATING_CAPACITY");
        testRoomWithSeatingCapacity.setNumber("TEST_NUMBER_WITH_SEATING_CAPACITY");
        testRoomWithSeatingCapacity.setActive(true);
        testRoomWithSeatingCapacity.setRoomSeatingCapacities(Set.of(roomSeatingCapacity));

        roomSeatingCapacity.setRoom(testRoomWithSeatingCapacity);

        when(personService.resolveInternalPersonByOrganisationIDOrThrowException(any()))
                .thenReturn(mockPerson);

        when(bookingRepository.findById(any())).thenReturn(Optional.of(baseBooking));

        when(bookingRepository.saveAndFlush(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

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

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void seatingTypeNotAvailableInRoom_ShouldReturnFalse_WhenSeatingTypeIsNull() {
        Booking booking = new Booking();
        booking.setSeatingType(null);

        assertFalse(bookingService.seatingTypeNotAvailableInRoom(booking));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void seatingTypeNotAvailableInRoom_ShouldReturnTrue_WhenRoomIsNull() {
        Booking booking = new Booking();
        SeatingType seatingType = new SeatingType();
        seatingType.setName("Parlamentarisch");
        seatingType.setId(UUID.randomUUID());
        booking.setSeatingType(seatingType);
        booking.setRoom(null);

        assertTrue(bookingService.seatingTypeNotAvailableInRoom(booking));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void seatingTypeNotAvailableInRoom_ShouldReturnTrue_WhenRoomHasNoCapacities() {
        Booking booking = new Booking();
        SeatingType seatingType = new SeatingType();
        seatingType.setName("Block");
        seatingType.setId(UUID.randomUUID());
        booking.setSeatingType(seatingType);

        Room room = new Room();
        room.setRoomSeatingCapacities(Collections.emptySet());
        booking.setRoom(room);

        assertTrue(bookingService.seatingTypeNotAvailableInRoom(booking));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void seatingTypeNotAvailableInRoom_ShouldReturnTrue_WhenSeatingTypeIsNotInRoomCapacities() {
        Booking booking = new Booking();
        SeatingType requestedType = new SeatingType();
        requestedType.setId(UUID.randomUUID());
        requestedType.setName("Kreis");
        booking.setSeatingType(requestedType);

        SeatingType availableType = new SeatingType();
        availableType.setName("Block");
        availableType.setId(UUID.randomUUID());

        RoomSeatingCapacity capacity = new RoomSeatingCapacity();
        capacity.setSeatingType(availableType);

        Room room = new Room();
        room.setRoomSeatingCapacities(Set.of(capacity));
        booking.setRoom(room);

        assertTrue(bookingService.seatingTypeNotAvailableInRoom(booking));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void seatingTypeNotAvailableInRoom_ShouldReturnFalse_WhenSeatingTypeExistsInRoomCapacities() {
        Booking booking = new Booking();
        SeatingType requestedType = new SeatingType();
        requestedType.setName("Block");
        requestedType.setId(UUID.randomUUID());
        booking.setSeatingType(requestedType);

        RoomSeatingCapacity capacity = new RoomSeatingCapacity();
        capacity.setSeatingType(requestedType);

        Room room = new Room();
        room.setRoomSeatingCapacities(Set.of(capacity));
        booking.setRoom(room);

        assertFalse(bookingService.seatingTypeNotAvailableInRoom(booking));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void shouldBeValidWhenParticipantCountIsZero() {
        baseBooking.setParticipantCount(0);

        Booking createdBooking = bookingService.createBooking(baseBooking);

        assertNotNull(createdBooking);
        assertEquals(0, createdBooking.getParticipantCount());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void shouldBeValidWhenCountIsWithinRoomCapacity() {
        testRoom.setCapacity(10);
        baseBooking.setRoom(testRoom);
        baseBooking.setParticipantCount(5);

        Booking booking = bookingService.createBooking(baseBooking);
        assertNotNull(booking);
        assertEquals(5, booking.getParticipantCount());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void shouldBeInvalidWhenCountExceedsRoomCapacity() {
        testRoom.setCapacity(10);
        baseBooking.setRoom(testRoom);
        baseBooking.setParticipantCount(11);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingService.createBooking(baseBooking));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_PARTICIPANT_COUNT_INVALID + "\"", exception.getMessage());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void shouldBeValidWhenNoRoomAndCountIsWithinAbsoluteMax() {
        baseBooking.setRoom(null);
        baseBooking.setParticipantCount(50);
        when(roomService.findAbsoluteMaxCapacity()).thenReturn(100);

        Booking booking = bookingService.createBooking(baseBooking);
        assertNotNull(booking);
        assertEquals(50, booking.getParticipantCount());
        verify(roomService).findAbsoluteMaxCapacity();
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void shouldBeInvalidWhenNoRoomAndCountExceedsAbsoluteMax() {
        baseBooking.setRoom(null);
        baseBooking.setParticipantCount(101);
        when(roomService.findAbsoluteMaxCapacity()).thenReturn(100);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingService.createBooking(baseBooking));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_PARTICIPANT_COUNT_INVALID + "\"", exception.getMessage());

        verify(roomService).findAbsoluteMaxCapacity();
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void shouldBeInvalidWhenCountExceedsSeatingCapacity() {

        baseBooking.setRoom(testRoomWithSeatingCapacity);
        baseBooking.setParticipantCount(11);
        baseBooking.setSeatingType(testSeatingType);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingService.createBooking(baseBooking));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_PARTICIPANT_COUNT_INVALID + "\"", exception.getMessage());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void shouldBeValidWhenCountIsWithinSeatingCapacity() {
        baseBooking.setRoom(testRoomWithSeatingCapacity);
        baseBooking.setParticipantCount(9);
        baseBooking.setSeatingType(testSeatingType);

        Booking booking = bookingService.createBooking(baseBooking);
        assertNotNull(booking);
        assertEquals(9, booking.getParticipantCount());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void shouldBeInvalidWhenRoomIsInactive() {
        baseBooking.setRoom(testRoomInactive);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingService.createBooking(baseBooking));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_ROOM_INACTIVE + "\"", exception.getMessage());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void shouldBeInvalidWhenSeatingTypeNotAvailableInRoom() {
        baseBooking.setRoom(testRoomWithSeatingCapacity);
        SeatingType alternativeSeatingType = new SeatingType();
        alternativeSeatingType.setName("ALTERNATIVE_TEST_SEATING");
        alternativeSeatingType.setId(UUID.randomUUID());
        baseBooking.setSeatingType(alternativeSeatingType);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingService.createBooking(baseBooking));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_SEATINGTYPE_NOT_AVAILABLE + "\"", exception.getMessage());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void shouldBeValidWhenSameRoomEvenIfInactive() {
        baseBooking.setRoom(testRoomInactive);
        baseBooking.setParticipantCount(9);
        baseBooking.setSeatingType(testSeatingType);

        when(bookingRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(baseBooking));
        Booking booking = bookingService.updateBooking(baseBooking, UUID.randomUUID());
        assertNotNull(booking);
        assertEquals(9, booking.getParticipantCount());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void shouldBeValidWhenUpdateFromInactiveToActiveRoom() {
        baseBooking.setRoom(testRoomInactive);
        baseBooking.setParticipantCount(9);
        baseBooking.setSeatingType(testSeatingType);

        when(bookingRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(baseBooking));
        baseBooking.setRoom(testRoomWithSeatingCapacity);

        Booking booking = bookingService.updateBooking(baseBooking, UUID.randomUUID());
        assertNotNull(booking);
        assertEquals(9, booking.getParticipantCount());
    }

}
