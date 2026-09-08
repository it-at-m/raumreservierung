package de.muenchen.raumreservierung.room;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.muenchen.raumreservierung.booking.events.FutureBookingCheckEvent;
import de.muenchen.raumreservierung.booking.events.RemoveRoomFromBookingsEvent;
import de.muenchen.raumreservierung.common.ConflictException;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.person.domain.Person;
import de.muenchen.raumreservierung.seating.SeatingType;
import jakarta.persistence.EntityManager;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private RoomService roomService;

    @Test
    public void givenRoom_thenReturnsCorrectEntity_whenCreateRoomOnlyEquipment() {
        // Given
        final Room roomRequest = getExampleRoom();

        // When
        final Equipment equipment1 = getEquipment1();
        final Equipment equipment2 = getEquipment2();
        final Set<Equipment> equipmentsFull = Set.of(equipment1, equipment2);

        final Room roomFull = buildExampleRoomWithEquipmentAndSeating(getExampleRoom(), null, equipmentsFull);

        final UUID generatedId = UUID.randomUUID();
        roomFull.setId(generatedId);

        when(roomRepository.saveAndFlush(any(Room.class))).thenReturn(roomFull);
        when(roomRepository.findWithDetailsById(generatedId)).thenReturn(Optional.of(roomFull));

        final Room result = roomService.createRoom(roomRequest);

        // Then
        assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(roomFull);
    }

    @Test
    public void givenRoom_thenReturnsCorrectEntity_whenCreateRoomOnlySeatingCapacity() {
        // Given
        final Room roomRequest = getExampleRoom();

        // When
        final RoomSeatingCapacity seatingCapacityOnlyId1 = getRoomSeatingCapacity(roomRequest);
        final RoomSeatingCapacity seatingCapacityOnlyId2 = getRoomSeatingCapacity2(roomRequest);

        final Set<RoomSeatingCapacity> seatingCapacitiesOnlyIds = new HashSet<>(Set.of(seatingCapacityOnlyId1, seatingCapacityOnlyId2));

        final Room roomFull = buildExampleRoomWithEquipmentAndSeating(getExampleRoom(), seatingCapacitiesOnlyIds, null);

        final UUID generatedId = UUID.randomUUID();
        roomFull.setId(generatedId);

        when(roomRepository.saveAndFlush(any(Room.class))).thenReturn(roomFull);
        when(roomRepository.findWithDetailsById(generatedId)).thenReturn(Optional.of(roomFull));

        final Room result = roomService.createRoom(roomRequest);

        // Then
        assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(roomFull);
    }

    @Test
    public void givenInactiveRoomWithoutFutureBooking_whenDeleteRoom_thenPublishesEventAndDeletes() {
        // Given
        final UUID roomId = UUID.randomUUID();
        final Room roomToDelete = getExampleRoom();
        roomToDelete.setId(roomId);
        roomToDelete.setActive(false);

        when(roomRepository.findWithDetailsById(roomId)).thenReturn(Optional.of(roomToDelete));

        // When
        roomService.deleteRoom(roomId);

        // Then
        verify(roomRepository).deleteById(roomId);
        final ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(2)).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getAllValues())
                .anyMatch(event -> event instanceof FutureBookingCheckEvent)
                .anyMatch(event -> event instanceof RemoveRoomFromBookingsEvent);
    }

    @Test
    public void givenActiveRoom_whenDeleteRoom_thenThrowsConflictExceptionAndDoesNotDelete() {
        // Given
        final UUID roomId = UUID.randomUUID();
        final Room activeRoom = getExampleRoom();
        activeRoom.setId(roomId);
        activeRoom.setActive(true);

        when(roomRepository.findWithDetailsById(roomId)).thenReturn(Optional.of(activeRoom));

        // When / Then
        assertThatThrownBy(() -> roomService.deleteRoom(roomId))
                .isInstanceOf(ConflictException.class);

        verify(roomRepository, never()).deleteById(any());
        verify(eventPublisher, never()).publishEvent(any(RemoveRoomFromBookingsEvent.class));
    }

    @Test
    public void givenInactiveRoomWithFutureBooking_whenDeleteRoom_thenThrowsConflictExceptionAndDoesNotDelete() {
        // Given
        final UUID roomId = UUID.randomUUID();
        final Room roomToDelete = getExampleRoom();
        roomToDelete.setId(roomId);
        roomToDelete.setActive(false);

        when(roomRepository.findWithDetailsById(roomId)).thenReturn(Optional.of(roomToDelete));

        doAnswer(invocation -> {
            final FutureBookingCheckEvent event = invocation.getArgument(0);
            event.setFutureBookingExists(true);
            return null;
        }).when(eventPublisher).publishEvent(any(FutureBookingCheckEvent.class));

        // When / Then
        assertThatThrownBy(() -> roomService.deleteRoom(roomId))
                .isInstanceOf(ConflictException.class);

        verify(roomRepository, never()).deleteById(any());
        verify(eventPublisher, never()).publishEvent(any(RemoveRoomFromBookingsEvent.class));
    }

    @Test
    public void givenUnknownRoomId_whenDeleteRoom_thenThrowsNotFoundExceptionAndDoesNothingElse() {
        // Given
        final UUID roomId = UUID.randomUUID();
        when(roomRepository.findWithDetailsById(roomId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> roomService.deleteRoom(roomId))
                .isInstanceOf(NotFoundException.class);

        verify(roomRepository, never()).deleteById(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    public void givenNoListenerMarksFutureBooking_whenExistsFutureBookingForRoom_thenReturnsFalse() {
        // Given
        final UUID roomId = UUID.randomUUID();

        // When
        final boolean result = roomService.existsFutureBookingForRoom(roomId);

        // Then
        assertThat(result).isFalse();
        final ArgumentCaptor<FutureBookingCheckEvent> eventCaptor = ArgumentCaptor.forClass(FutureBookingCheckEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getRoomId()).isEqualTo(roomId);
    }

    @Test
    public void givenListenerMarksFutureBooking_whenExistsFutureBookingForRoom_thenReturnsTrue() {
        // Given
        final UUID roomId = UUID.randomUUID();
        doAnswer(invocation -> {
            final FutureBookingCheckEvent event = invocation.getArgument(0);
            event.setFutureBookingExists(true);
            return null;
        }).when(eventPublisher).publishEvent(any(FutureBookingCheckEvent.class));

        // When
        final boolean result = roomService.existsFutureBookingForRoom(roomId);

        // Then
        assertThat(result).isTrue();
        verify(eventPublisher).publishEvent(any(FutureBookingCheckEvent.class));
    }

    private Room buildExampleRoomWithEquipmentAndSeating(Room room, Set<RoomSeatingCapacity> seatingCapacities, Set<Equipment> equipments) {
        room.setRoomSeatingCapacities(seatingCapacities);
        room.setEquipment(equipments);
        return room;
    }

    private Room getExampleRoom() {
        final Person contactPerson = new InternalPerson();
        contactPerson.setEmail("hans.dampf@muenchen.de");
        contactPerson.setFirstName("Hans");
        contactPerson.setLastName("Dampf");
        final Room roomRequest = new Room();
        roomRequest.setName("Mittlerer Saal");
        roomRequest.setNumber("102");
        roomRequest.setLocation("Pfad 3, 10101 Dazwischen, Deutschland");
        roomRequest.setCapacity(500);
        roomRequest.setActive(true);
        roomRequest.setArea(100);
        roomRequest.setContactPerson(contactPerson);

        return roomRequest;
    }

    private SeatingType getSeatingType1() {
        SeatingType seatingType1 = new SeatingType();
        seatingType1.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        return seatingType1;
    }

    private SeatingType getSeatingType2() {
        SeatingType seatingType2 = new SeatingType();
        seatingType2.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174002"));
        return seatingType2;
    }

    private Equipment getEquipment1() {
        Equipment equipment1 = new Equipment();
        equipment1.setName("Tisch");
        equipment1.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        equipment1.setDescription("Ein stabiler Holzschreibtisch mit viel Platz für Arbeiten.");
        return equipment1;
    }

    private Equipment getEquipment2() {
        Equipment equipment2 = new Equipment();
        equipment2.setName("Stuhl");
        equipment2.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"));
        equipment2.setDescription("Ein ergonomischer Bürostuhl mit verstellbarer Höhe.");
        return equipment2;
    }

    private RoomSeatingCapacity getRoomSeatingCapacity(Room room) {
        final RoomSeatingCapacity roomSeatingCapacity1 = new RoomSeatingCapacity();
        roomSeatingCapacity1.setRoom(room);
        roomSeatingCapacity1.setSeatingType(getSeatingType1());
        roomSeatingCapacity1.setCapacity(100);
        return roomSeatingCapacity1;
    }

    private RoomSeatingCapacity getRoomSeatingCapacity2(Room room) {
        final RoomSeatingCapacity roomSeatingCapacity2 = new RoomSeatingCapacity();
        roomSeatingCapacity2.setRoom(room);
        roomSeatingCapacity2.setSeatingType(getSeatingType2());
        roomSeatingCapacity2.setCapacity(200);
        return roomSeatingCapacity2;
    }

}
