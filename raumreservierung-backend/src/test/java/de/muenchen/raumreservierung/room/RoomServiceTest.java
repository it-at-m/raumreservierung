package de.muenchen.raumreservierung.room;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.equipment.EquipmentRepository;
import de.muenchen.raumreservierung.seating.SeatingRepository;
import de.muenchen.raumreservierung.seating.SeatingType;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
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
    private EquipmentRepository equipmentRepository;

    @Mock
    private SeatingRepository seatingRepository;

    @InjectMocks
    private RoomService roomService;

    @Test
    public void givenRoom_thenReturnsCorrectEntity_whenCreateRoom() {
        // Given
        UUID stId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID stId2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
        UUID eId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID eId2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");

        final Room roomRequest = buildExampleRoomWithEquipmentAndSeating(getExampleRoom(), getSeatingTypesOnlyIds(stId1, stId2),
                getEquipmentsOnlyIds(eId1, eId2));

        // When
        final SeatingType seatingType1 = getSeatingType1();
        final SeatingType seatingType2 = getSeatingType2();
        final Equipment equipment1 = getEquipment1();
        final Equipment equipment2 = getEquipment2();
        final Set<SeatingType> seatingTypesFull = Set.of(seatingType1, seatingType2);
        final Set<Equipment> equipmentsFull = Set.of(equipment1, equipment2);
        final Room roomFull = buildExampleRoomWithEquipmentAndSeating(getExampleRoom(), seatingTypesFull, equipmentsFull);

        when(equipmentRepository.findById(eId1)).thenReturn(Optional.of(equipment1));
        when(equipmentRepository.findById(eId2)).thenReturn(Optional.of(equipment2));
        when(seatingRepository.findById(stId1)).thenReturn(Optional.of(seatingType1));
        when(seatingRepository.findById(stId2)).thenReturn(Optional.of(seatingType2));
        when(roomRepository.save(roomRequest)).thenReturn(roomFull);
        final Room result = roomService.createRoom(roomRequest);

        // Then
        assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(roomFull);
    }

    @Test
    public void givenRoom_thenReturnsCorrectEntity_whenUpdateRoom() {
        // Given
        UUID roomId = UUID.fromString("123e4567-e89b-12d3-a456-426614175000");
        UUID stId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        SeatingType seatingType1 = getSeatingType1();
        SeatingType seatingType2 = getSeatingType2();
        Equipment equipment1 = getEquipment1();
        Equipment equipment2 = getEquipment2();

        final Set<SeatingType> seatingTypesFull = Set.of(seatingType1, seatingType2);
        final Set<Equipment> equipmentsFull = Set.of(equipment1, equipment2);
        final Room roomExisting = buildExampleRoomWithEquipmentAndSeating(getExampleRoom(), seatingTypesFull, equipmentsFull);

        final Set<SeatingType> seatingTypesUpdateFull = getSeatingTypesOnlyIds(stId1);
        final Set<Equipment> equipmentsUpdateFull = getEquipmentsOnlyIds();
        final Room roomUpdateFull = buildExampleRoomWithEquipmentAndSeating(getExampleRoom2(), seatingTypesUpdateFull, equipmentsUpdateFull);

        final Set<SeatingType> seatingTypesUpdate = getSeatingTypesOnlyIds(stId1);
        final Set<Equipment> equipmentsUpdate = getEquipmentsOnlyIds();
        final Room roomUpdate = buildExampleRoomWithEquipmentAndSeating(getExampleRoom2(), seatingTypesUpdate, equipmentsUpdate);

        // When
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(roomExisting));
        when(seatingRepository.findById(stId1)).thenReturn(Optional.of(seatingType1));
        when(roomRepository.save(roomUpdate)).thenReturn(roomUpdateFull);

        final Room result = roomService.updateRoom(roomUpdate, roomId);

        // Then
        assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(roomUpdateFull);
    }

    @Test
    public void givenRoom_thenReturnsCorrectEntity_whenUpdateRoomWithNullEquipmentAndSeatingType() {
        // Given
        UUID roomId = UUID.fromString("123e4567-e89b-12d3-a456-426614175000");

        SeatingType seatingType1 = getSeatingType1();
        SeatingType seatingType2 = getSeatingType2();
        Equipment equipment1 = getEquipment1();
        Equipment equipment2 = getEquipment2();

        final Set<SeatingType> seatingTypesFull = Set.of(seatingType1, seatingType2);
        final Set<Equipment> equipmentsFull = Set.of(equipment1, equipment2);
        final Room roomExisting = buildExampleRoomWithEquipmentAndSeating(getExampleRoom(), seatingTypesFull, equipmentsFull);

        final Room roomUpdate = buildExampleRoomWithEquipmentAndSeating(getExampleRoom2(), null, null);

        // When
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(roomExisting));
        when(roomRepository.save(roomUpdate)).thenReturn(roomUpdate);

        final Room result = roomService.updateRoom(roomUpdate, roomId);

        // Then
        assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(roomUpdate);
    }

    private Room buildExampleRoomWithEquipmentAndSeating(Room room, Set<SeatingType> seatingTypes, Set<Equipment> equipments) {
        room.setSeatingType(seatingTypes);
        room.setEquipment(equipments);
        return room;
    }

    private <T> Set<T> convertIdsToSet(List<UUID> ids, Supplier<T> supplier, BiConsumer<T, UUID> biConsumer) {
        return ids.stream().map(id -> {
            T element = supplier.get();
            biConsumer.accept(element, id);
            return element;
        }).collect(Collectors.toSet());
    }

    private Room getExampleRoom() {
        final Room roomRequest = new Room();
        roomRequest.setName("Mittlerer Saal");
        roomRequest.setNumber("102");
        roomRequest.setAddress("Pfad 3, 10101 Dazwischen, Deutschland");
        roomRequest.setCapacity(500);
        roomRequest.setInformation("Ein mittelgroßer Saal mit Stühlen, Tischen und Reihenbestuhlung und Stehempfang.");
        roomRequest.setNote("Hier gibt es keine Flecken.");
        roomRequest.setAvailability(true);
        roomRequest.setArea(100);

        return roomRequest;
    }

    private Room getExampleRoom2() {
        final Room roomRequest = new Room();
        roomRequest.setName("Mittel großer Saal");
        roomRequest.setNumber("103");
        roomRequest.setAddress("Pfad 30, 10101 Dazwischen, Deutschland");
        roomRequest.setCapacity(5000);
        roomRequest.setInformation("Ein Saal mit Stühlen.");
        roomRequest.setNote("Hier gibt es keine größeren Flecken.");
        roomRequest.setAvailability(false);
        roomRequest.setArea(1000);

        return roomRequest;
    }

    private Set<SeatingType> getSeatingTypesOnlyIds(UUID... ids) {
        return convertIdsToSet(List.of(ids), SeatingType::new, SeatingType::setId);
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

    private Set<Equipment> getEquipmentsOnlyIds(UUID... ids) {
        return convertIdsToSet(List.of(ids), Equipment::new, Equipment::setId);
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

}
