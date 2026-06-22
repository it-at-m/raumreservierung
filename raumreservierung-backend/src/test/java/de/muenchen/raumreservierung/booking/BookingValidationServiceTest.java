package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_EQUIPMENT_INACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_SEATINGTYPE_INACTIVE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
                SecurityConfiguration.class,
                SecurityContextService.class
        }
)
public class BookingValidationServiceTest {
    @Autowired
    private BookingValidationService bookingValidationService;
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
    private SeatingType testSeatingTypeInactive;
    private Equipment testEquipment;
    private Equipment testEquipmentInactive;

    @BeforeEach
    void setUp() {
        InternalPerson mockPerson = new InternalPerson();
        mockPerson.setOrganisationId("000001");
        mockPerson.setId(UUID.fromString("12345678-abcd-ef01-2345-6789abcdef01"));

        baseBooking = new Booking();
        baseBooking.setTitle("TEST_BOOKING");
        baseBooking.setBookedBy(mockPerson);

        baseBookingExisting = new Booking();
        baseBookingExisting.setTitle("TEST_BOOKING");
        baseBookingExisting.setBookedBy(mockPerson);

        testRoom = new Room();
        testRoom.setName("TEST_ROOM");
        testRoom.setNumber("TEST_NUMBER");
        testRoom.setActive(true);

        testEquipment = new Equipment();
        testEquipment.setActive(true);
        testEquipment.setName("TEST_EQUIPMENT");
        testEquipment.setId(UUID.randomUUID());
        testEquipmentInactive = new Equipment();
        testEquipmentInactive.setActive(false);
        testEquipmentInactive.setName("TEST_EQUIPMENT_INACTIVE");
        testEquipmentInactive.setId(UUID.randomUUID());

        testSeatingType = new SeatingType();
        testSeatingType.setName("TEST_SEATING");
        testSeatingType.setActive(true);
        testSeatingType.setId(UUID.randomUUID());
        testSeatingTypeInactive = new SeatingType();
        testSeatingTypeInactive.setName("TEST_SEATING_2");
        testSeatingTypeInactive.setActive(false);
        testSeatingTypeInactive.setId(UUID.randomUUID());
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

        when(personService.getInternalPersonByOrganisationIDOrThrowException(any()))
                .thenReturn(mockPerson);

        when(bookingRepository.findById(any())).thenReturn(Optional.of(baseBooking));

        when(bookingRepository.saveAndFlush(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
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
        baseBooking.setSeatingType(testSeatingTypeInactive);
        baseBookingExisting.setSeatingType(testSeatingTypeInactive);

        assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking, baseBookingExisting));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void seatingTypeInactive_ShouldThrow_WhenUpdatingWithInactiveSeatingType() {
        baseBooking.setSeatingType(testSeatingTypeInactive);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingValidationService.bookingIsValidOrThrowException(baseBooking));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_SEATINGTYPE_INACTIVE + "\"", exception.getMessage());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void seatingTypeInactive_ShouldThrow_WhenUpdatingFromActiveToInactiveSeatingType() {
        baseBooking.setSeatingType(testSeatingTypeInactive);
        baseBookingExisting.setSeatingType(testSeatingType);

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
        baseBooking.setEquipment(new HashSet<>(List.of(testEquipment, testEquipmentInactive)));
        baseBookingExisting.setEquipment(new HashSet<>(List.of(testEquipmentInactive)));

        assertDoesNotThrow(() -> bookingValidationService.bookingIsValidOrThrowException(baseBooking, baseBookingExisting));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void equipmentInactive_ShouldThrow_WhenUpdatingActiveEquipmentToInactiveEquipment() {
        baseBooking.setEquipment(new HashSet<>(List.of(testEquipmentInactive)));
        baseBookingExisting.setEquipment(new HashSet<>(List.of(testEquipment)));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingValidationService.bookingIsValidOrThrowException(baseBooking, baseBookingExisting));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_EQUIPMENT_INACTIVE + "\"", exception.getMessage());
    }
}
