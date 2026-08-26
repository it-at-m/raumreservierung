package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_EQUIPMENT_INACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_PARTICIPANT_COUNT_INVALID;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_ROOM_INACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_SEATINGTYPE_INACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_SEATINGTYPE_NOT_AVAILABLE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_STATUS_CHANGE_NOT_POSSIBLE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_UPDATE_OF_FIELDS_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
                BookingPersistenceHelper.class,
                SecurityConfiguration.class,
                SecurityContextService.class
        }
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingValidationServiceTest {
    @Autowired
    private BookingValidationService bookingValidationService;
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
    private Booking baseBookingExisting;
    private Room testRoom;
    private Room testRoomInactive;
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

        testSeatingType2Inactive = new SeatingType();
        testSeatingType2Inactive.setName("TEST_SEATING_2");
        testSeatingType2Inactive.setActive(false);
        testSeatingType2Inactive.setId(UUID.randomUUID());

        RoomSeatingCapacity roomSeatingCapacityForTestRoom = new RoomSeatingCapacity();
        roomSeatingCapacityForTestRoom.setCapacity(10);
        roomSeatingCapacityForTestRoom.setSeatingType(testSeatingType);
        roomSeatingCapacityForTestRoom.setRoom(testRoom);

        RoomSeatingCapacity roomSeatingCapacityForTestRoomInactive = new RoomSeatingCapacity();
        roomSeatingCapacityForTestRoomInactive.setCapacity(10);
        roomSeatingCapacityForTestRoomInactive.setSeatingType(testSeatingType);
        roomSeatingCapacityForTestRoom.setRoom(testRoomInactive);

        RoomSeatingCapacity roomSeatingCapacity2 = new RoomSeatingCapacity();
        roomSeatingCapacity2.setCapacity(10);
        roomSeatingCapacity2.setSeatingType(testSeatingType2Inactive);
        roomSeatingCapacityForTestRoom.setRoom(testRoom);

        testRoom = new Room();
        testRoom.setName("TEST_ROOM");
        testRoom.setNumber("TEST_NUMBER");
        testRoom.setActive(true);
        testRoom.setRoomSeatingCapacities(Set.of(roomSeatingCapacityForTestRoom, roomSeatingCapacity2));

        testRoomInactive = new Room();
        testRoomInactive.setName("TEST_ROOM_INACTIVE");
        testRoomInactive.setNumber("TEST_NUMBER_INACTIVE");
        testRoomInactive.setActive(false);
        testRoomInactive.setRoomSeatingCapacities(Set.of(roomSeatingCapacityForTestRoom));

        when(personService.resolveInternalPersonByOrganisationIDOrThrowException(anyString()))
                .thenReturn(testPerson);
        when(personService.getInternalPersonByOrganisationIDOrThrowException(anyString()))
                .thenReturn(testPerson);

        when(bookingRepository.findById(any())).thenReturn(Optional.of(baseBooking));

        when(bookingRepository.saveAndFlush(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @ParameterizedTest
    @MethodSource("provideParticipantCountData")
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void validateParticipantCount(Consumer<Booking> setup, String expectedErrorMsg) {
        setup.accept(baseBooking);

        if (expectedErrorMsg == null) {
            assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));
        } else {
            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));
            assertEquals(HttpStatus.BAD_REQUEST + " \"" + expectedErrorMsg + "\"", exception.getMessage());
        }
    }

    private Stream<Arguments> provideParticipantCountData() {
        return Stream.of(
                // Valid: 0 Participants
                Arguments.of((Consumer<Booking>) b -> b.setParticipantCount(0), null),

                // Invalid: Negative
                Arguments.of((Consumer<Booking>) b -> b.setParticipantCount(-1), MSG_PARTICIPANT_COUNT_INVALID),

                // Valid: Within room capacity (10)
                Arguments.of((Consumer<Booking>) b -> {
                    b.setRoom(testRoom);
                    testRoom.setCapacity(10);
                    b.setParticipantCount(5);
                }, null),

                // Invalid: Exceeds room capacity (10)
                Arguments.of((Consumer<Booking>) b -> {
                    b.setRoom(testRoom);
                    testRoom.setCapacity(10);
                    b.setParticipantCount(11);
                }, MSG_PARTICIPANT_COUNT_INVALID),

                // Valid: No room, within absolute max (100)
                Arguments.of((Consumer<Booking>) b -> {
                    b.setRoom(null);
                    b.setParticipantCount(50);
                    when(roomService.findAbsoluteMaxCapacity()).thenReturn(100);
                }, null),

                // Invalid: No room, exceeds absolute max
                Arguments.of((Consumer<Booking>) b -> {
                    b.setRoom(null);
                    b.setParticipantCount(101);
                    when(roomService.findAbsoluteMaxCapacity()).thenReturn(100);
                }, MSG_PARTICIPANT_COUNT_INVALID),

                // Valid: Within seating capacity (10)
                Arguments.of((Consumer<Booking>) b -> {
                    b.setRoom(testRoom);
                    b.setSeatingType(testSeatingType);
                    b.setParticipantCount(9);
                }, null),

                // Invalid: Exceeds seating capacity (10)
                Arguments.of((Consumer<Booking>) b -> {
                    b.setRoom(testRoom);
                    b.setSeatingType(testSeatingType);
                    b.setParticipantCount(11);
                }, MSG_PARTICIPANT_COUNT_INVALID));
    }

    @ParameterizedTest
    @MethodSource("provideSeatingTypeAvailabilityData")
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void validateSeatingTypeAvailability(Consumer<Booking> setup, String expectedErrorMsg) {
        setup.accept(baseBooking);

        if (expectedErrorMsg == null) {
            assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));
        } else {
            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));
            assertEquals(HttpStatus.BAD_REQUEST + " \"" + expectedErrorMsg + "\"", exception.getMessage());
        }
    }

    private Stream<Arguments> provideSeatingTypeAvailabilityData() {
        return Stream.of(
                // Valid: No seating type requested
                Arguments.of((Consumer<Booking>) b -> b.setSeatingType(null), null),

                // Invalid: Seating type requested, but no room selected
                Arguments.of((Consumer<Booking>) b -> {
                    b.setSeatingType(testSeatingType);
                    b.setRoom(null);
                }, MSG_SEATINGTYPE_NOT_AVAILABLE),

                // Invalid: Room has no configured capacities
                Arguments.of((Consumer<Booking>) b -> {
                    b.setSeatingType(testSeatingType);
                    testRoom.setRoomSeatingCapacities(Collections.emptySet());
                    b.setRoom(testRoom);
                }, MSG_SEATINGTYPE_NOT_AVAILABLE),

                // Invalid: Requested seating type not in room's capacities
                Arguments.of((Consumer<Booking>) b -> {
                    SeatingType different = new SeatingType();
                    different.setId(UUID.randomUUID());
                    different.setActive(true);
                    b.setSeatingType(different);
                    b.setRoom(testRoom);
                }, MSG_SEATINGTYPE_NOT_AVAILABLE),

                // Valid: Requested seating type is available in room
                Arguments.of((Consumer<Booking>) b -> {
                    b.setSeatingType(testSeatingType);
                    b.setRoom(testRoom);
                }, null));
    }

    @ParameterizedTest
    @MethodSource("provideSeatingTypeInactiveData")
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void validateSeatingTypeInactive(BiConsumer<Booking, Booking> setup, String expectedErrorMsg) {
        setup.accept(baseBooking, baseBookingExisting);

        if (expectedErrorMsg == null) {
            assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking, baseBookingExisting));
        } else {
            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));
            assertEquals(HttpStatus.BAD_REQUEST + " \"" + expectedErrorMsg + "\"", exception.getMessage());
        }
    }

    private Stream<Arguments> provideSeatingTypeInactiveData() {
        return Stream.of(
                // Valid: No seating type requested and old seating type active
                Arguments.of((BiConsumer<Booking, Booking>) (newBooking, existingBooking) -> {
                    newBooking.setSeatingType(null);
                    existingBooking.setSeatingType(testSeatingType);
                }, null),

                // Valid: Old inactive seating type requested again
                Arguments.of((BiConsumer<Booking, Booking>) (newBooking, existingBooking) -> {
                    newBooking.setSeatingType(testSeatingType2Inactive);
                    newBooking.setRoom(testRoom);
                    existingBooking.setSeatingType(testSeatingType2Inactive);
                    existingBooking.setRoom(testRoom);
                }, null),

                // Invalid: Update with inactive seating type
                Arguments.of((BiConsumer<Booking, Booking>) (newBooking, existingBooking) -> {
                    newBooking.setSeatingType(testSeatingType2Inactive);
                    newBooking.setRoom(testRoom);
                    existingBooking.setSeatingType(null);
                    existingBooking.setRoom(null);
                }, MSG_SEATINGTYPE_INACTIVE),

                // Invalid: From active seating type to inactive
                Arguments.of((BiConsumer<Booking, Booking>) (newBooking, existingBooking) -> {
                    newBooking.setSeatingType(testSeatingType2Inactive);
                    newBooking.setRoom(testRoom);
                    existingBooking.setSeatingType(testSeatingType);
                    existingBooking.setRoom(testRoom);
                }, MSG_SEATINGTYPE_INACTIVE));
    }

    @ParameterizedTest
    @MethodSource("provideEquipmentInactiveData")
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void validateEquipmentInactive(BiConsumer<Booking, Booking> setup, String expectedErrorMsg) {
        setup.accept(baseBooking, baseBookingExisting);

        if (expectedErrorMsg == null) {
            assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking, baseBookingExisting));
        } else {
            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));
            assertEquals(HttpStatus.BAD_REQUEST + " \"" + expectedErrorMsg + "\"", exception.getMessage());
        }
    }

    private Stream<Arguments> provideEquipmentInactiveData() {
        return Stream.of(
                // Valid: no equipment requested and old equipment inactive
                Arguments.of((BiConsumer<Booking, Booking>) (newBooking, existingBooking) -> {
                    newBooking.setEquipment(new HashSet<>());
                    existingBooking.setEquipment(new HashSet<>(List.of(testEquipmentInactive)));
                }, null),

                // Valid: Updating from inactive equipment to same equipment
                Arguments.of((BiConsumer<Booking, Booking>) (newBooking, existingBooking) -> {
                    newBooking.setEquipment(new HashSet<>(List.of(testEquipment, testEquipment2Inactive)));
                    existingBooking.setEquipment(new HashSet<>(List.of(testEquipment2Inactive)));
                }, null),

                // Valid: Updating from inactive equipment to same equipment
                Arguments.of((BiConsumer<Booking, Booking>) (newBooking, existingBooking) -> {
                    newBooking.setEquipment(new HashSet<>(List.of(testEquipment2Inactive)));
                    existingBooking.setEquipment(new HashSet<>(List.of(testEquipment, testEquipment2Inactive)));
                }, null),

                // Invalid: Update with inactive equipment from active
                Arguments.of((BiConsumer<Booking, Booking>) (newBooking, existingBooking) -> {
                    newBooking.setEquipment(new HashSet<>(List.of(testEquipmentInactive)));
                    existingBooking.setEquipment(new HashSet<>(List.of(testEquipment)));
                }, MSG_EQUIPMENT_INACTIVE),

                // Invalid: From inactive seating type to other inactive
                Arguments.of((BiConsumer<Booking, Booking>) (newBooking, existingBooking) -> {
                    newBooking.setEquipment(new HashSet<>(List.of(testEquipment2Inactive)));
                    existingBooking.setEquipment(new HashSet<>(List.of(testEquipmentInactive)));
                }, MSG_EQUIPMENT_INACTIVE));
    }

    @ParameterizedTest
    @MethodSource("provideRoomInactiveData")
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void validateRoomInactive(BiConsumer<Booking, Booking> setup, String expectedErrorMsg) {
        setup.accept(baseBooking, baseBookingExisting);

        if (expectedErrorMsg == null) {
            assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking, baseBookingExisting));
        } else {
            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));
            assertEquals(HttpStatus.BAD_REQUEST + " \"" + expectedErrorMsg + "\"", exception.getMessage());
        }
    }

    private Stream<Arguments> provideRoomInactiveData() {
        return Stream.of(
                // Valid: no room requested and old room inactive
                Arguments.of((BiConsumer<Booking, Booking>) (newBooking, existingBooking) -> {
                    newBooking.setRoom(null);
                    existingBooking.setRoom(testRoomInactive);
                }, null),

                // Valid: Updating from inactive room to same room
                Arguments.of((BiConsumer<Booking, Booking>) (newBooking, existingBooking) -> {
                    newBooking.setRoom(testRoomInactive);
                    existingBooking.setRoom(testRoomInactive);
                }, null),

                // Invalid: Update with inactive room from active
                Arguments.of((BiConsumer<Booking, Booking>) (newBooking, existingBooking) -> {
                    newBooking.setRoom(testRoomInactive);
                    existingBooking.setRoom(testRoom);
                }, MSG_ROOM_INACTIVE));

    }

    @Test
    @WithMockJwt(lhmObjectID = "987654", authorities = { Roles.RAUM_ADMIN })
    void validateBookingAccess_ShouldReturnTrue_WhenAdmin() {
        Booking booking = new Booking();
        InternalPerson person = new InternalPerson();
        person.setOrganisationId("12345");
        booking.setBookedBy(person);

        assertTrue(bookingValidationService.validateBookingAuthority(booking, Roles.TERMIN_ORGANISATOR));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void validateBookingAccess_ShouldReturnTrue_WhenOIDMatches() {
        Booking booking = new Booking();
        InternalPerson person = new InternalPerson();
        person.setOrganisationId("000001");
        person.setId(UUID.fromString("12345678-abcd-ef01-2345-6789abcdef01"));
        booking.setBookedBy(person);

        when(personService.getInternalPersonByOrganisationIDOrThrowException(securityContextService.getCurrentOID()))
                .thenReturn(person);

        assertTrue(bookingValidationService.validateBookingAuthority(booking, Roles.RAUM_BUCHUNG));
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

        when(personService.getInternalPersonByOrganisationIDOrThrowException(securityContextService.getCurrentOID()))
                .thenReturn(currentUser);

        assertFalse(bookingValidationService.validateBookingAuthority(booking, Roles.RAUM_ADMIN));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void hasBookingAccess_ShouldThrow_WhenBookingHasNoOwner() {
        Booking booking = new Booking();
        booking.setBookedBy(null);

        assertThrows(NullPointerException.class, () -> bookingValidationService.validateBookingAuthority(booking, Roles.RAUM_ADMIN));
    }

    @ParameterizedTest
    @MethodSource("provideTerminalStatusData")
    @WithMockJwt(lhmObjectID = "000002", authorities = { Roles.RAUM_ADMIN })
    void validateTerminalStatusOrThrowException(BiConsumer<Booking, Booking> setup, String expectedErrorMsg) {
        setup.accept(baseBooking, baseBookingExisting);

        if (expectedErrorMsg == null) {
            assertDoesNotThrow(() -> bookingValidationService.validateTerminalStatusOrThrowException(baseBooking, baseBookingExisting));
        } else {
            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> bookingValidationService.validateTerminalStatusOrThrowException(baseBooking, baseBookingExisting));
            assertEquals(HttpStatus.BAD_REQUEST + " \"" + expectedErrorMsg + "\"", exception.getMessage());
        }
    }

    private Stream<Arguments> provideTerminalStatusData() {
        return Stream.of(

                // Valid: existingBooking is null -> method returns without checking anything
                Arguments.of((BiConsumer<Booking, Booking>) (newBooking, existingBooking) -> existingBooking = null, null),

                // Valid: only status is changed to UNFEASIBLE, all other fields untouched
                Arguments.of((BiConsumer<Booking, Booking>) (newBooking, existingBooking) -> {
                    newBooking.setId(existingBooking.getId());
                    newBooking.setStatus(BookingStatus.UNFEASIBLE);
                    newBooking.setReasonForStatusChange("REASON_FOR_REJECTION");
                }, null),

                // Valid: only status is changed to CANCELED, all other fields untouched
                Arguments.of((BiConsumer<Booking, Booking>) (newBooking, existingBooking) -> {
                    newBooking.setId(existingBooking.getId());
                    newBooking.setStatus(BookingStatus.CANCELED);
                }, null),

                // Invalid: status changed to UNFEASIBLE but an unrelated field is also modified
                Arguments.of((BiConsumer<Booking, Booking>) (newBooking, existingBooking) -> {
                    newBooking.setId(existingBooking.getId());
                    newBooking.setStatus(BookingStatus.UNFEASIBLE);
                    newBooking.setTitle("NEW_TITLE");
                }, MSG_UPDATE_OF_FIELDS_NOT_ALLOWED),

                // Invalid: status changed to CANCELED but an unrelated field is also modified
                Arguments.of((BiConsumer<Booking, Booking>) (newBooking, existingBooking) -> {
                    newBooking.setId(existingBooking.getId());
                    newBooking.setStatus(BookingStatus.CANCELED);
                    newBooking.setParticipantCount(4);
                }, MSG_UPDATE_OF_FIELDS_NOT_ALLOWED),

                // Invalid: reasonForStatusChange is set even though status is not UNFEASIBLE
                Arguments.of((BiConsumer<Booking, Booking>) (newBooking, existingBooking) -> {
                    newBooking.setId(existingBooking.getId());
                    newBooking.setStatus(existingBooking.getStatus());
                    newBooking.setReasonForStatusChange("Some reason without status change");
                }, MSG_UPDATE_OF_FIELDS_NOT_ALLOWED));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void validateTerminalStatusOrThrowException_anwenderSetsreasonForStatusChange_throwsException() {
        baseBooking.setId(baseBookingExisting.getId());
        baseBooking.setStatus(baseBookingExisting.getStatus());
        baseBooking.setReasonForStatusChange("REASON_FOR_REJECTION");

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingValidationService.validateTerminalStatusOrThrowException(baseBooking, baseBookingExisting));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_UPDATE_OF_FIELDS_NOT_ALLOWED + "\"", exception.getMessage());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void validateBookingStatusTransitionOrThrowException_shouldThrow_whenAnwenderAndNotAllowedTransition() {
        baseBooking.setId(baseBookingExisting.getId());
        baseBooking.setStatus(BookingStatus.UNFEASIBLE);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingValidationService.validateBookingStatusTransitionOrThrowException(baseBookingExisting, baseBooking));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_STATUS_CHANGE_NOT_POSSIBLE + "\"", exception.getMessage());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void validateBookingStatusTransitionOrThrowException_shouldBeValid_whenAnwenderAndAllowedTransition() {
        baseBooking.setId(baseBookingExisting.getId());
        baseBooking.setStatus(BookingStatus.CANCELED);

        assertDoesNotThrow(() -> bookingValidationService.validateBookingStatusTransitionOrThrowException(baseBookingExisting, baseBooking));

    }

}
