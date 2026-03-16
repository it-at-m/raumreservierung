package de.muenchen.raumreservierung.room;

import static org.assertj.core.api.Assertions.assertThat;

import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.equipment.dto.EquipmentMapperImpl;
import de.muenchen.raumreservierung.room.dto.RoomMapper;
import de.muenchen.raumreservierung.room.dto.RoomMapperImpl;
import de.muenchen.raumreservierung.room.dto.RoomRequestDTO;
import de.muenchen.raumreservierung.room.dto.RoomResponseDTO;
import de.muenchen.raumreservierung.seating.SeatingType;
import de.muenchen.raumreservierung.seating.dto.SeatingTypeMapperImpl;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = {
                EquipmentMapperImpl.class,
                SeatingTypeMapperImpl.class,
                RoomMapperImpl.class
        }
)
public class RoomMapperTest {

    @Autowired
    private RoomMapper roomMapper;

    @Test
    public void givenRequestDTO_thenReturnsCorrectEntity() {
        // Given
        UUID seatingTypeId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID seatingTypeId2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
        final Set<UUID> seatingTypeIds = Set.of(seatingTypeId1, seatingTypeId2);

        UUID equipmentId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID equipmentId2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        final Set<UUID> equipmentIds = Set.of(equipmentId1, equipmentId2);

        final RoomRequestDTO requestDTO = new RoomRequestDTO("Mittlerer Saal", "102", "Pfad 3, 10101 Dazwischen, Deutschland", 500,
                "Ein mittelgroßer Saal mit Stühlen, Tischen und Reihenbestuhlung und Stehempfang.", "Hier gibt es keine Flecken.", true, 100,
                seatingTypeIds, equipmentIds);

        // When
        final Room result = roomMapper.toEntity(requestDTO);

        // Then
        Equipment equipment1 = new Equipment();
        equipment1.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        Equipment equipment2 = new Equipment();
        equipment2.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"));
        final Set<Equipment> equipments = Set.of(equipment1, equipment2);

        assertThat(result).usingRecursiveComparison().ignoringFields("id", "equipment", "seatingType").isEqualTo(requestDTO);
        assertThat(result.getEquipment()).usingRecursiveComparison().isEqualTo(equipments);

    }

    @Test
    public void givenEntity_thenReturnsCorrectResponseDTO() {
        // Given
        SeatingType seatingType1 = new SeatingType();
        seatingType1.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        SeatingType seatingType2 = new SeatingType();
        seatingType2.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174002"));
        final Set<SeatingType> seatingTypes = Set.of(seatingType1, seatingType2);

        Equipment equipment1 = new Equipment();
        equipment1.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        Equipment equipment2 = new Equipment();
        equipment2.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"));
        final Set<Equipment> equipments = Set.of(equipment1, equipment2);

        final Room room = new Room();
        room.setName("Mittlerer Saal");
        room.setNumber("102");
        room.setAddress("Pfad 3, 10101 Dazwischen, Deutschland");
        room.setCapacity(500);
        room.setInformation("Ein mittelgroßer Saal mit Stühlen, Tischen und Reihenbestuhlung und Stehempfang.");
        room.setNote("Hier gibt es keine Flecken.");
        room.setAvailability(true);
        room.setArea(100);
        room.setSeatingType(seatingTypes);
        room.setEquipment(equipments);

        // When
        final RoomResponseDTO result = roomMapper.toDTO(room);

        // Then
        assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(room);
    }
}
