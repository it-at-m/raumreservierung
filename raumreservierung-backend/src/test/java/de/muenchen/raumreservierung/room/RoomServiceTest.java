package de.muenchen.raumreservierung.room;

import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.equipment.EquipmentService;
import de.muenchen.raumreservierung.seating.SeatingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private EquipmentService equipmentService;

    @Mock
    private RoomSeatingCapacityService roomSeatingCapacityService;

    @InjectMocks
    private RoomService roomService;

    @Test
    public void givenRoom_thenReturnsCorrectEntity_whenCreateRoomOnlyEquipment() {
        // Given
        UUID eId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID eId2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");

        final Room roomRequest = getExampleRoom();

        // When
        final Equipment equipment1 = getEquipment1();
        final Equipment equipment2 = getEquipment2();
        final Set<Equipment> equipmentsFull = Set.of(equipment1, equipment2);
        final Room roomFull = buildExampleRoomWithEquipmentAndSeating(getExampleRoom(), null, equipmentsFull);

        when(roomRepository.save(roomRequest)).thenReturn(roomFull);
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

        final List<RoomSeatingCapacity> seatingCapacitiesOnlyIds = new ArrayList<>(List.of(seatingCapacityOnlyId1, seatingCapacityOnlyId2));

        final Room roomFull = buildExampleRoomWithEquipmentAndSeating(roomRequest, seatingCapacitiesOnlyIds, null);

        when(roomRepository.save(roomRequest)).thenReturn(roomFull);
        final Room result = roomService.createRoom(roomRequest);

        // Then
        assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(roomFull);
    }

    private Room buildExampleRoomWithEquipmentAndSeating(Room room, List<RoomSeatingCapacity> seatingCapacities, Set<Equipment> equipments) {
        room.setRoomSeatingCapacities(seatingCapacities);
        room.setEquipment(equipments);
        return room;
    }

    private Room getExampleRoom() {
        final Room roomRequest = new Room();
        roomRequest.setName("Mittlerer Saal");
        roomRequest.setNumber("102");
        roomRequest.setAddress("Pfad 3, 10101 Dazwischen, Deutschland");
        roomRequest.setCapacity(500);
        roomRequest.setInformation("Ein mittelgroßer Saal mit Stühlen, Tischen und Reihenbestuhlung und Stehempfang.");
        roomRequest.setNote("Hier gibt es keine Flecken.");
        roomRequest.setIsActive(true);
        roomRequest.setArea(100);

        return roomRequest;
    }

    private SeatingType getSeatingType1() {
        SeatingType seatingType1 = new SeatingType();
        seatingType1.setName("Reihenbestuhlung");
        seatingType1.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        seatingType1.setDescription("Beschreibung von Reihenbestuhlung");
        return seatingType1;
    }

    private SeatingType getSeatingType2() {
        SeatingType seatingType2 = new SeatingType();
        seatingType2.setName("Stehempfang");
        seatingType2.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174002"));
        seatingType2.setDescription("Beschreibung von Stehempfang");
        return seatingType2;
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

    private RoomSeatingCapacity getRoomSeatingCapacity1(Room room) {
        final RoomSeatingCapacity roomSeatingCapacity1 = new RoomSeatingCapacity();
        roomSeatingCapacity1.setRoom(room);
        roomSeatingCapacity1.setSeatingType(getSeatingType1());
        roomSeatingCapacity1.setCapacity(100);
        return roomSeatingCapacity1;
    }

    private RoomSeatingCapacity getRoomSeatingCapacityOnlyId1(Room room) {
        final RoomSeatingCapacity roomSeatingCapacity1 = new RoomSeatingCapacity();
        roomSeatingCapacity1.setRoom(room);
        roomSeatingCapacity1.setSeatingType(getSeatingTypeOnlyId1());
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

    private RoomSeatingCapacity getRoomSeatingCapacityOnlyId2(Room room) {
        final RoomSeatingCapacity roomSeatingCapacity2 = new RoomSeatingCapacity();
        roomSeatingCapacity2.setRoom(room);
        roomSeatingCapacity2.setSeatingType(getSeatingTypeOnlyId2());
        roomSeatingCapacity2.setCapacity(200);
        return roomSeatingCapacity2;
    }

}
