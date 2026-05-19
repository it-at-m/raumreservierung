package de.muenchen.raumreservierung.room;

import static org.assertj.core.api.Assertions.assertThat;

import de.muenchen.raumreservierung.common.ReferenceMapper;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.equipment.dto.EquipmentMapperImpl;
import de.muenchen.raumreservierung.person.domain.ExternalPerson;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.person.domain.Person;
import de.muenchen.raumreservierung.person.dto.PersonMapper;
import de.muenchen.raumreservierung.person.dto.PersonMapperImpl;
import de.muenchen.raumreservierung.room.dto.RoomListResponseDTO;
import de.muenchen.raumreservierung.room.dto.RoomMapper;
import de.muenchen.raumreservierung.room.dto.RoomMapperImpl;
import de.muenchen.raumreservierung.room.dto.RoomRequestDTO;
import de.muenchen.raumreservierung.room.dto.SeatingCapacityRequestDTO;
import de.muenchen.raumreservierung.seating.SeatingType;
import de.muenchen.raumreservierung.seating.dto.SeatingTypeMapperImpl;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
        classes = {
                EquipmentMapperImpl.class,
                SeatingTypeMapperImpl.class,
                RoomMapperImpl.class,
                PersonMapperImpl.class,
        }
)
public class RoomMapperTest {

    @Autowired
    private RoomMapper roomMapper;

    @MockitoBean
    private PersonMapper personMapper;

    @MockitoBean
    private ReferenceMapper referenceMapper;

    @Test
    public void givenRequestDTO_thenReturnsCorrectEntity() {
        // Given
        UUID seatingTypeId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID seatingTypeId2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
        SeatingCapacityRequestDTO capacityRequestDTO1 = new SeatingCapacityRequestDTO(seatingTypeId1, 100);
        SeatingCapacityRequestDTO capacityRequestDTO2 = new SeatingCapacityRequestDTO(seatingTypeId2, 200);
        final Set<SeatingCapacityRequestDTO> capacityRequestDTOs = Set.of(capacityRequestDTO1, capacityRequestDTO2);

        UUID equipmentId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID equipmentId2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        final Set<UUID> equipmentIds = Set.of(equipmentId1, equipmentId2);

        final UUID personId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        final RoomRequestDTO requestDTO = new RoomRequestDTO("Mittlerer Saal", "102", "Pfad 3, 10101 Dazwischen, Deutschland", "Hinterm Stein links.", 500,
                true, 100,
                new ArrayList<>(capacityRequestDTOs), new ArrayList<>(equipmentIds), personId);

        // When
        SeatingType seatingTypeOnlyId1 = new SeatingType();
        seatingTypeOnlyId1.setId(seatingTypeId1);
        SeatingType seatingTypeOnlyId2 = new SeatingType();
        seatingTypeOnlyId2.setId(seatingTypeId2);
        Mockito.when(referenceMapper.resolve(seatingTypeId1, SeatingType.class)).thenReturn(seatingTypeOnlyId1);
        Mockito.when(referenceMapper.resolve(seatingTypeId2, SeatingType.class)).thenReturn(seatingTypeOnlyId2);

        Equipment equipment1 = new Equipment();
        equipment1.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        Equipment equipment2 = new Equipment();
        equipment2.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"));
        Mockito.when(referenceMapper.resolve(equipmentId1, Equipment.class)).thenReturn(equipment1);
        Mockito.when(referenceMapper.resolve(equipmentId2, Equipment.class)).thenReturn(equipment2);

        Person contactPerson = new ExternalPerson();
        contactPerson.setFirstName("Hans");
        contactPerson.setLastName("Dampf");
        contactPerson.setEmail("hans.dampf@muenchen.de");
        Mockito.when(referenceMapper.resolve(personId, Person.class)).thenReturn(contactPerson);

        final Room result = roomMapper.toEntity(requestDTO);

        // Then
        final Set<Equipment> equimentSet = Set.of(equipment1, equipment2);
        RoomSeatingCapacity roomSeatingCapacity1 = new RoomSeatingCapacity();
        roomSeatingCapacity1.setSeatingType(seatingTypeOnlyId1);
        roomSeatingCapacity1.setCapacity(100);
        RoomSeatingCapacity roomSeatingCapacity2 = new RoomSeatingCapacity();
        roomSeatingCapacity2.setSeatingType(seatingTypeOnlyId2);
        roomSeatingCapacity2.setCapacity(200);
        final Set<RoomSeatingCapacity> roomSeatingCapacitySet = Set.of(roomSeatingCapacity1, roomSeatingCapacity2);

        assertThat(result).usingRecursiveComparison().ignoringFields("id", "equipment", "roomSeatingCapacities", "bookedBy").isEqualTo(requestDTO);
        assertThat(result.getEquipment()).usingRecursiveComparison().isEqualTo(equimentSet);
        assertThat(result.getRoomSeatingCapacities()).usingRecursiveComparison().isEqualTo(roomSeatingCapacitySet);
        assertThat(result.getContactPerson()).usingRecursiveComparison().isEqualTo(contactPerson);

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
        final Set<RoomSeatingCapacity> roomSeatingCapacitySet = Set.of(roomSeatingCapacity1, roomSeatingCapacity2);

        Equipment equipment1 = new Equipment();
        equipment1.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        Equipment equipment2 = new Equipment();
        equipment2.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"));
        final Set<Equipment> equipments = Set.of(equipment1, equipment2);

        final Person contactPerson = new InternalPerson();
        contactPerson.setFirstName("Hans");
        contactPerson.setLastName("Dampf");
        contactPerson.setEmail("hans.dampf@muenchen.de");

        final Room room = new Room();
        room.setName("Mittlerer Saal");
        room.setNumber("102");
        room.setLocation("Pfad 3, 10101 Dazwischen, Deutschland");
        room.setCapacity(500);
        room.setActive(true);
        room.setArea(100);
        room.setRoomSeatingCapacities(roomSeatingCapacitySet);
        room.setEquipment(equipments);
        room.setContactPerson(contactPerson);

        // When
        final RoomListResponseDTO result = roomMapper.toListDTO(room);

        // Then
        assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(room);
    }
}
