package de.muenchen.raumreservierung.room;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.equipment.EquipmentRepository;
import de.muenchen.raumreservierung.seating.SeatingRepository;
import de.muenchen.raumreservierung.seating.SeatingType;
import java.util.Optional;
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
    private EquipmentRepository equipmentRepository;

    @Mock
    private SeatingRepository seatingRepository;

    @InjectMocks
    private RoomService roomService;

    @Test
    public void givenRoom_thenReturnsCorrectEntity_whenCreateRoom() {
        // Given
        SeatingType seatingTypeRequest1 = new SeatingType();
        UUID stId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        seatingTypeRequest1.setId(stId1);
        SeatingType seatingTypeRequest2 = new SeatingType();
        UUID stId2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
        seatingTypeRequest2.setId(stId2);
        final Set<SeatingType> seatingTypesRequest = Set.of(seatingTypeRequest1, seatingTypeRequest2);

        Equipment equipmentRequest1 = new Equipment();
        UUID eId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        equipmentRequest1.setId(eId1);
        Equipment equipmentRequest2 = new Equipment();
        UUID eId2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        equipmentRequest2.setId(eId2);
        final Set<Equipment> equipmentsRequest = Set.of(equipmentRequest1, equipmentRequest2);

        final Room roomRequest = new Room();
        roomRequest.setName("Mittlerer Saal");
        roomRequest.setNumber("102");
        roomRequest.setAddress("Pfad 3, 10101 Dazwischen, Deutschland");
        roomRequest.setCapacity(500);
        roomRequest.setInformation("Ein mittelgroßer Saal mit Stühlen, Tischen und Reihenbestuhlung und Stehempfang.");
        roomRequest.setNote("Hier gibt es keine Flecken.");
        roomRequest.setAvailability(true);
        roomRequest.setArea(100);
        roomRequest.setSeatingType(seatingTypesRequest);
        roomRequest.setEquipment(equipmentsRequest);

        // When

        SeatingType seatingType1 = new SeatingType();
        seatingType1.setName("Reihenbestuhlung");
        seatingType1.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        seatingType1.setDescription("Beschreibung von Reihenbestuhlung");
        SeatingType seatingType2 = new SeatingType();
        seatingType2.setName("Stehempfang");
        seatingType2.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174002"));
        seatingType2.setDescription("Beschreibung von Stehempfang");
        final Set<SeatingType> seatingTypes = Set.of(seatingType1, seatingType2);

        Equipment equipment1 = new Equipment();
        equipment1.setName("Tisch");
        equipment1.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        equipment1.setDescription("Ein stabiler Holzschreibtisch mit viel Platz für Arbeiten.");
        Equipment equipment2 = new Equipment();
        equipment2.setName("Stuhl");
        equipment2.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"));
        equipment2.setDescription("Ein ergonomischer Bürostuhl mit verstellbarer Höhe.");
        final Set<Equipment> equipments = Set.of(equipment1, equipment2);

        when(equipmentRepository.findById(eId1)).thenReturn(Optional.of(equipment1));
        when(equipmentRepository.findById(eId2)).thenReturn(Optional.of(equipment2));
        when(seatingRepository.findById(stId1)).thenReturn(Optional.of(seatingType1));
        when(seatingRepository.findById(stId2)).thenReturn(Optional.of(seatingType2));
        when(roomRepository.save(roomRequest)).thenReturn(roomRequest);
        final Room result = roomService.createRoom(roomRequest);

        // Then
        assertThat(result).usingRecursiveComparison().ignoringFields("id", "seatingType", "equipment").isEqualTo(roomRequest);
        assertThat(result.getSeatingType()).usingRecursiveComparison().isEqualTo(seatingTypes);
        assertThat(result.getEquipment()).usingRecursiveComparison().isEqualTo(equipments);
    }

}
