package de.muenchen.raumreservierung.room;

import de.muenchen.raumreservierung.common.BaseEntity;
import de.muenchen.raumreservierung.common.ReferenceMapper;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.equipment.dto.EquipmentMapperImpl;
import de.muenchen.raumreservierung.room.dto.RoomListResponseDTO;
import de.muenchen.raumreservierung.room.dto.RoomMapper;
import de.muenchen.raumreservierung.room.dto.RoomMapperImpl;
import de.muenchen.raumreservierung.room.dto.RoomRequestDTO;
import de.muenchen.raumreservierung.room.dto.SeatingCapacityRequestDTO;
import de.muenchen.raumreservierung.seating.SeatingType;
import de.muenchen.raumreservierung.seating.dto.SeatingTypeMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {
                EquipmentMapperImpl.class,
                SeatingTypeMapperImpl.class,
                RoomMapperImpl.class,
                RoomMapperTest.TestConfig.class
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
        SeatingCapacityRequestDTO capacityRequestDTO1 = new SeatingCapacityRequestDTO(seatingTypeId1, 100);
        SeatingCapacityRequestDTO capacityRequestDTO2 = new SeatingCapacityRequestDTO(seatingTypeId2, 200);
        final List<SeatingCapacityRequestDTO> capacityRequestDTOs = List.of(capacityRequestDTO1, capacityRequestDTO2);

        UUID equipmentId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID equipmentId2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        final Set<UUID> equipmentIds = Set.of(equipmentId1, equipmentId2);

        final RoomRequestDTO requestDTO = new RoomRequestDTO("Mittlerer Saal", "102", "Pfad 3, 10101 Dazwischen, Deutschland", 500,
                "Ein mittelgroßer Saal mit Stühlen, Tischen und Reihenbestuhlung und Stehempfang.", "Hier gibt es keine Flecken.", true, 100,
                capacityRequestDTOs, equipmentIds);

        // When
        final Room result = roomMapper.toEntity(requestDTO);

        // Then
        Equipment equipment1 = new Equipment();
        equipment1.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        Equipment equipment2 = new Equipment();
        equipment2.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"));

        SeatingType seatingTypeOnlyId1 = new SeatingType();
        seatingTypeOnlyId1.setId(seatingTypeId1);
        SeatingType seatingTypeOnlyId2 = new SeatingType();
        seatingTypeOnlyId2.setId(seatingTypeId2);
        RoomSeatingCapacity roomSeatingCapacity1 = new RoomSeatingCapacity();
        roomSeatingCapacity1.setSeatingType(seatingTypeOnlyId1);
        roomSeatingCapacity1.setCapacity(100);
        RoomSeatingCapacity roomSeatingCapacity2 = new RoomSeatingCapacity();
        roomSeatingCapacity2.setSeatingType(seatingTypeOnlyId2);
        roomSeatingCapacity2.setCapacity(200);
        final Set<RoomSeatingCapacity> roomSeatingCapacitySet = Set.of(roomSeatingCapacity1, roomSeatingCapacity2);

        assertThat(result).usingRecursiveComparison().ignoringFields("id", "equipment", "roomSeatingCapacities").isEqualTo(requestDTO);
        assertThat(result.getEquipment()).usingRecursiveComparison().isNull();
        assertThat(result.getRoomSeatingCapacities()).usingRecursiveComparison().isEqualTo(roomSeatingCapacitySet);

    }

    @Test
    public void givenEntity_thenReturnsCorrectResponseDTO() {
        // Given
        SeatingType seatingType1 = new SeatingType();
        seatingType1.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        SeatingType seatingType2 = new SeatingType();
        seatingType2.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174002"));
        RoomSeatingCapacity roomSeatingCapacity1 = new RoomSeatingCapacity();
        roomSeatingCapacity1.setSeatingType(seatingType1);
        RoomSeatingCapacity roomSeatingCapacity2 = new RoomSeatingCapacity();
        roomSeatingCapacity2.setSeatingType(seatingType2);
        final List<RoomSeatingCapacity> roomSeatingCapacitySet = List.of(roomSeatingCapacity1, roomSeatingCapacity2);

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
        room.setIsActive(true);
        room.setArea(100);
        room.setRoomSeatingCapacities(roomSeatingCapacitySet);
        room.setEquipment(equipments);

        // When
        final RoomListResponseDTO result = roomMapper.toDTO(room);

        // Then
        assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(room);
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public ReferenceMapper referenceMapper() {
            return new ReferenceMapper() {
                @Override
                public <T extends BaseEntity> T resolve(java.util.UUID id, Class<T> entityClass) {
                    return null;
                }
            };
        }
    }
}
