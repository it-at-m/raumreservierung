package de.muenchen.raumreservierung.booking;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muenchen.raumreservierung.appointment.AppointmentService;
import de.muenchen.raumreservierung.person.PersonService;
import de.muenchen.raumreservierung.room.Room;
import de.muenchen.raumreservierung.room.RoomSeatingCapacity;
import de.muenchen.raumreservierung.seating.SeatingType;
import de.muenchen.raumreservierung.security.SecurityContextService;
import jakarta.persistence.EntityManager;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BookingServiceUnitTest {

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        this.bookingService = new BookingService(
                Mockito.mock(BookingRepository.class),
                Mockito.mock(EntityManager.class),
                Mockito.mock(SecurityContextService.class),
                Mockito.mock(AppointmentService.class),
                Mockito.mock(PersonService.class));
    }

    @Test
    void seatingTypeNotAvailableInRoom_ShouldReturnFalse_WhenSeatingTypeIsNull() {
        Booking booking = new Booking();
        booking.setSeatingType(null);

        assertFalse(bookingService.seatingTypeNotAvailableInRoom(booking));
    }

    @Test
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
}
