package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_EQUIPMENT_INACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_PARTICIPANT_COUNT_INVALID;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_ROOM_INACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_SEATINGTYPE_INACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_SEATINGTYPE_NOT_AVAILABLE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.muenchen.raumreservierung.appointment.AppointmentService;
import de.muenchen.raumreservierung.common.BadRequestException;
import de.muenchen.raumreservierung.configuration.security.SecurityConfiguration;
import de.muenchen.raumreservierung.equipment.Equipment;
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
import java.util.HashSet;
import java.util.List;
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
                BookingTransitionService.class,
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
    private Booking baseBookingExisting;
    private Room testRoom;
    private Room testRoomInactive;
    private Room testRoomWithSeatingCapacity;
    private SeatingType testSeatingType;
    private SeatingType testSeatingType2Inactive;
    private Equipment testEquipment;
    private Equipment testEquipmentInactive;
    private Equipment testEquipment2Inactive;

    @BeforeEach
    void setUp() {
        InternalPerson testPerson = new InternalPerson();
        testPerson.setOrganisationId("000001");
        testPerson.setId(UUID.fromString("12345678-abcd-ef01-2345-6789abcdef01"));

        baseBooking = new Booking();
        baseBooking.setTitle("TEST_BOOKING");
        baseBooking.setBookedBy(testPerson);
        baseBooking.setStatus(BookingStatus.NEW);

        baseBookingExisting = new Booking();
        baseBookingExisting.setTitle("TEST_BOOKING");
        baseBookingExisting.setBookedBy(testPerson);
        baseBookingExisting.setStatus(BookingStatus.NEW);

        UUID testEquipmentId = UUID.randomUUID();
        testEquipment = new Equipment();
        testEquipment.setActive(true);
        testEquipment.setName("TEST_EQUIPMENT");
        testEquipment.setId(testEquipmentId);

        testEquipmentInactive = new Equipment();
        testEquipmentInactive.setActive(false);
        testEquipmentInactive.setName("TEST_EQUIPMENT");
        testEquipmentInactive.setId(testEquipmentId);

        testEquipment2Inactive = new Equipment();
        testEquipment2Inactive.setActive(false);
        testEquipment2Inactive.setName("TEST_EQUIPMENT_2_INACTIVE");
        testEquipment2Inactive.setId(UUID.randomUUID());

        UUID testSeatingTypeId = UUID.randomUUID();
        testSeatingType = new SeatingType();
        testSeatingType.setName("TEST_SEATING");
        testSeatingType.setActive(true);
        testSeatingType.setId(testSeatingTypeId);

        SeatingType testSeatingTypeInactive = new SeatingType();
        testSeatingTypeInactive.setName("TEST_SEATING");
        testSeatingTypeInactive.setActive(false);
        testSeatingTypeInactive.setId(testSeatingTypeId);

        testSeatingType2Inactive = new SeatingType();
        testSeatingType2Inactive.setName("TEST_SEATING_2");
        testSeatingType2Inactive.setActive(false);
        testSeatingType2Inactive.setId(UUID.randomUUID());

        RoomSeatingCapacity roomSeatingCapacity = new RoomSeatingCapacity();
        roomSeatingCapacity.setCapacity(10);
        roomSeatingCapacity.setSeatingType(testSeatingType);

        RoomSeatingCapacity roomSeatingCapacity2 = new RoomSeatingCapacity();
        roomSeatingCapacity2.setCapacity(10);
        roomSeatingCapacity2.setSeatingType(testSeatingType2Inactive);

        testRoom = new Room();
        testRoom.setName("TEST_ROOM");
        testRoom.setNumber("TEST_NUMBER");
        testRoom.setActive(true);
        testRoom.setRoomSeatingCapacities(Set.of(roomSeatingCapacity, roomSeatingCapacity2));

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

        when(personService.resolveInternalPersonByOrganisationIDOrThrowException(anyString()))
                .thenReturn(testPerson);
        when(personService.getInternalPersonByOrganisationIDOrThrowException(anyString()))
                .thenReturn(testPerson);

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
        differentSeatingType.setActive(true);
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
        alternativeSeatingType.setActive(true);
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

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void seatingTypeInactive_ShouldBeValid_WhenNoExistingSeatingTypeAndUpdateSeatingTypeIsNull() {
        baseBooking.setSeatingType(null);

        assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void seatingTypeInactive_ShouldBeValid_WhenExistingSeatingTypeNotNullAndUpdateSeatingTypeIsNull() {
        baseBooking.setSeatingType(null);
        baseBookingExisting.setSeatingType(testSeatingType);

        assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking, baseBookingExisting));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void seatingTypeInactive_ShouldBeValid_WhenUpdatingWithSameInactiveSeatingType() {
        baseBooking.setSeatingType(testSeatingType2Inactive);
        baseBooking.setRoom(testRoom);
        baseBookingExisting.setSeatingType(testSeatingType2Inactive);
        baseBookingExisting.setRoom(testRoom);

        assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking, baseBookingExisting));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void seatingTypeInactive_ShouldThrow_WhenUpdatingWithInactiveSeatingType() {
        baseBookingExisting.setSeatingType(testSeatingType);
        baseBookingExisting.setRoom(testRoom);
        baseBooking.setSeatingType(testSeatingType2Inactive);
        baseBooking.setRoom(testRoom);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingValidationService.bookingIsValidOrThrowException(baseBooking, baseBookingExisting));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_SEATINGTYPE_INACTIVE + "\"", exception.getMessage());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void seatingTypeInactive_ShouldThrow_WhenUpdatingFromActiveToInactiveSeatingType() {
        baseBooking.setSeatingType(testSeatingType2Inactive);
        baseBooking.setRoom(testRoom);
        baseBookingExisting.setSeatingType(testSeatingType);
        baseBookingExisting.setRoom(testRoom);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingValidationService.bookingIsValidOrThrowException(baseBooking, baseBookingExisting));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_SEATINGTYPE_INACTIVE + "\"", exception.getMessage());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void equipmentInactive_ShouldBeValid_WhenUpdatingEquipmentIsNull() {
        baseBooking.setEquipment(new HashSet<>());
        baseBookingExisting.setEquipment(new HashSet<>(List.of(testEquipment)));

        assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking, baseBookingExisting));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void equipmentInactive_ShouldBeValid_WhenUpdatingNoEquipment() {
        baseBooking.setEquipment(new HashSet<>());

        assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void equipmentInactive_ShouldBeValid_WhenUpdatingFromInactiveEquipmentToSameInactiveEquipment() {
        baseBooking.setEquipment(new HashSet<>(List.of(testEquipment, testEquipment2Inactive)));
        baseBookingExisting.setEquipment(new HashSet<>(List.of(testEquipment2Inactive)));

        assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking, baseBookingExisting));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void equipmentInactive_ShouldThrow_WhenUpdatingActiveEquipmentToInactiveEquipment() {
        baseBookingExisting.setEquipment(new HashSet<>(List.of(testEquipment)));
        baseBooking.setEquipment(new HashSet<>(List.of(testEquipmentInactive)));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingValidationService.bookingIsValidOrThrowException(baseBooking, baseBookingExisting));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_EQUIPMENT_INACTIVE + "\"", exception.getMessage());
    }
}
