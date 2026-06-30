package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_PARTICIPANT_COUNT_INVALID;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_ROOM_INACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_SEATINGTYPE_NOT_AVAILABLE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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
                BookingValidationService.class,
                SecurityConfiguration.class,
                SecurityContextService.class
        }
)
public class BookingValidationServiceTest {
    @Autowired
    private BookingValidationService bookingValidationService;
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
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void seatingTypeNotAvailableInRoom_ShouldBeValid_WhenSeatingTypeIsNull() {
        baseBooking.setSeatingType(null);

        assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void seatingTypeNotAvailableInRoom_ShouldThrow_WhenRoomIsNull() {
        baseBooking.setSeatingType(testSeatingType);
        baseBooking.setRoom(null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_SEATINGTYPE_NOT_AVAILABLE + "\"", exception.getMessage());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void seatingTypeNotAvailableInRoom_ShouldThrow_WhenRoomHasNoCapacities() {
        baseBooking.setSeatingType(testSeatingType);
        testRoom.setRoomSeatingCapacities(Collections.emptySet());
        baseBooking.setRoom(testRoom);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_SEATINGTYPE_NOT_AVAILABLE + "\"", exception.getMessage());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void seatingTypeNotAvailableInRoom_ShouldThrow_WhenSeatingTypeIsNotInRoomCapacities() {
        SeatingType differentSeatingType = new SeatingType();
        differentSeatingType.setId(UUID.randomUUID());
        differentSeatingType.setName("Kreis");
        baseBooking.setSeatingType(differentSeatingType);
        baseBooking.setRoom(testRoomWithSeatingCapacity);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_SEATINGTYPE_NOT_AVAILABLE + "\"", exception.getMessage());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void seatingTypeNotAvailableInRoom_ShouldBeValid_WhenSeatingTypeExistsInRoomCapacities() {
        baseBooking.setSeatingType(testSeatingType);
        baseBooking.setRoom(testRoomWithSeatingCapacity);

        assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void participantCountNotValid_ShouldBeValid_WhenParticipantCountIsZero() {
        baseBooking.setParticipantCount(0);

        assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));

        Booking createdBooking = bookingService.createBooking(baseBooking);

        assertNotNull(createdBooking);
        assertEquals(0, createdBooking.getParticipantCount());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void participantCountNotValid_ShouldThrow_WhenParticipantCountIsNegative() {
        baseBooking.setParticipantCount(-1);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_PARTICIPANT_COUNT_INVALID + "\"", exception.getMessage());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void participantCountNotValid_ShouldBeValid_WhenCountIsWithinRoomCapacity() {
        testRoom.setCapacity(10);
        baseBooking.setRoom(testRoom);
        baseBooking.setParticipantCount(5);

        assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));

        Booking booking = bookingService.createBooking(baseBooking);
        assertNotNull(booking);
        assertEquals(5, booking.getParticipantCount());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void participantCountNotValid_ShouldThrow_WhenCountExceedsRoomCapacity() {
        testRoom.setCapacity(10);
        baseBooking.setRoom(testRoom);
        baseBooking.setParticipantCount(11);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_PARTICIPANT_COUNT_INVALID + "\"", exception.getMessage());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void participantCountNotValid_ShouldBeValid_WhenNoRoomAndCountIsWithinAbsoluteMax() {
        baseBooking.setRoom(null);
        baseBooking.setParticipantCount(50);
        when(roomService.findAbsoluteMaxCapacity()).thenReturn(100);

        assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));

        Booking booking = bookingService.createBooking(baseBooking);
        assertNotNull(booking);
        assertEquals(50, booking.getParticipantCount());
        verify(roomService, times(2)).findAbsoluteMaxCapacity();
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void participantCountNotValid_ShouldThrow_WhenNoRoomAndCountExceedsAbsoluteMax() {
        baseBooking.setRoom(null);
        baseBooking.setParticipantCount(101);
        when(roomService.findAbsoluteMaxCapacity()).thenReturn(100);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_PARTICIPANT_COUNT_INVALID + "\"", exception.getMessage());

        verify(roomService).findAbsoluteMaxCapacity();
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void participantCountNotValid_ShouldThrow_WhenCountExceedsSeatingCapacity() {

        baseBooking.setRoom(testRoomWithSeatingCapacity);
        baseBooking.setParticipantCount(11);
        baseBooking.setSeatingType(testSeatingType);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_PARTICIPANT_COUNT_INVALID + "\"", exception.getMessage());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void participantCountNotValid_ShouldBeValid_WhenCountIsWithinSeatingCapacity() {
        baseBooking.setRoom(testRoomWithSeatingCapacity);
        baseBooking.setParticipantCount(9);
        baseBooking.setSeatingType(testSeatingType);

        assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));

        Booking booking = bookingService.createBooking(baseBooking);
        assertNotNull(booking);
        assertEquals(9, booking.getParticipantCount());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void bookingIsValid_ShouldThrow_WhenRoomIsInactive() {
        baseBooking.setRoom(testRoomInactive);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_ROOM_INACTIVE + "\"", exception.getMessage());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void seatingTypeNotAvailableInRoom_ShouldThrow_WhenSeatingTypeNotAvailableInRoom() {
        baseBooking.setRoom(testRoomWithSeatingCapacity);
        SeatingType alternativeSeatingType = new SeatingType();
        alternativeSeatingType.setName("ALTERNATIVE_TEST_SEATING");
        alternativeSeatingType.setId(UUID.randomUUID());
        baseBooking.setSeatingType(alternativeSeatingType);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_SEATINGTYPE_NOT_AVAILABLE + "\"", exception.getMessage());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void bookingIsValid_ShouldBeValid_WhenSameRoomEvenIfInactive() {
        baseBooking.setRoom(testRoomInactive);
        baseBooking.setParticipantCount(9);
        baseBooking.setSeatingType(testSeatingType);

        when(bookingRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(baseBooking));

        assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking, baseBooking));

        Booking booking = bookingService.updateBooking(baseBooking, UUID.randomUUID());
        assertNotNull(booking);
        assertEquals(9, booking.getParticipantCount());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void bookingIsValid_ShouldBeValid_WhenUpdateFromInactiveToActiveRoom() {
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
