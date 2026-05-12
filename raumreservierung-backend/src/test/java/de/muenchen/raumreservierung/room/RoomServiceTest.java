package de.muenchen.raumreservierung.room;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.person.domain.Person;
import de.muenchen.raumreservierung.seating.SeatingType;
import jakarta.persistence.EntityManager;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private EntityManager entityManager;

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
        when(roomRepository.findWithDetailsById(generatedId)).thenReturn(java.util.Optional.of(roomFull));

        final Room result = roomService.createRoom(roomRequest);

        // Then
        assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(roomFull);
    }

    @Test
    public void givenRoom_thenReturnsCorrectEntity_whenCreateRoomOnlySeatingCapacity() {
        // Given
        final Room roomRequest = getExampleRoom();

        // When
        final RoomSeatingCapacity seatingCapacityOnlyId1 = getRoomSeatingCapacityOnlyId1(roomRequest);
        final RoomSeatingCapacity seatingCapacityOnlyId2 = getRoomSeatingCapacityOnlyId2(roomRequest);

        final Set<RoomSeatingCapacity> seatingCapacitiesOnlyIds = new HashSet<>(Set.of(seatingCapacityOnlyId1, seatingCapacityOnlyId2));

        final Room roomFull = buildExampleRoomWithEquipmentAndSeating(getExampleRoom(), seatingCapacitiesOnlyIds, null);

        // WICHTIG: Dem Mock-Ergebnis eine ID geben!
        final UUID generatedId = UUID.randomUUID();
        roomFull.setId(generatedId);

        // Die neuen Service-Methoden mocken
        when(roomRepository.saveAndFlush(any(Room.class))).thenReturn(roomFull);
        when(roomRepository.findWithDetailsById(generatedId)).thenReturn(java.util.Optional.of(roomFull));

        final Room result = roomService.createRoom(roomRequest);

        // Then
        assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(roomFull);
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

    private SeatingType getSeatingTypeOnlyId1() {
        SeatingType seatingType1 = new SeatingType();
        seatingType1.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        return seatingType1;
    }

    private SeatingType getSeatingTypeOnlyId2() {
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

    private RoomSeatingCapacity getRoomSeatingCapacityOnlyId1(Room room) {
        final RoomSeatingCapacity roomSeatingCapacity1 = new RoomSeatingCapacity();
        roomSeatingCapacity1.setRoom(room);
        roomSeatingCapacity1.setSeatingType(getSeatingTypeOnlyId1());
        roomSeatingCapacity1.setCapacity(100);
        return roomSeatingCapacity1;
    }

    private RoomSeatingCapacity getRoomSeatingCapacityOnlyId2(Room room) {
        final RoomSeatingCapacity roomSeatingCapacity2 = new RoomSeatingCapacity();
        roomSeatingCapacity2.setRoom(room);
        roomSeatingCapacity2.setSeatingType(getSeatingTypeOnlyId2());
        roomSeatingCapacity2.setCapacity(200);
        return roomSeatingCapacity2;
    }

}
