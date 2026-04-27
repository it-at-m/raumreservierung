package de.muenchen.raumreservierung.room;

import static de.muenchen.raumreservierung.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.raumreservierung.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muenchen.raumreservierung.MicroServiceApplication;
import de.muenchen.raumreservierung.TestConstants;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.equipment.EquipmentRepository;
import de.muenchen.raumreservierung.person.InternalPerson;
import de.muenchen.raumreservierung.person.PersonRepository;
import de.muenchen.raumreservierung.person.dto.InternalPersonResponseDto;
import de.muenchen.raumreservierung.room.dto.RoomDetailsResponseDTO;
import de.muenchen.raumreservierung.room.dto.RoomListResponseDTO;
import de.muenchen.raumreservierung.room.dto.RoomRequestDTO;
import de.muenchen.raumreservierung.room.dto.SeatingCapacityRequestDTO;
import de.muenchen.raumreservierung.seating.SeatingRepository;
import de.muenchen.raumreservierung.seating.SeatingType;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class RoomControllerIntegrationTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private static final String ROOMS_URL = "/rooms";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private SeatingRepository seatingTypeRepository;

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private RoomRepository roomRepository;

    @Test
    void testCreateAndGetRoom() {
        final RoomRequestDTO request = new RoomRequestDTO(
                "Raum 1",
                "1",
                "Straße 1",
                "Ganz hinten links",
                2000,
                true,
                500,
                Set.of(),
                Set.of(),
                null);

        ResponseEntity<RoomDetailsResponseDTO> createResponse = testRestTemplate.postForEntity(
                ROOMS_URL,
                request,
                RoomDetailsResponseDTO.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        UUID roomId = createResponse.getBody().id();

        ResponseEntity<RoomDetailsResponseDTO> getResponse = testRestTemplate.getForEntity(
                ROOMS_URL + "/" + roomId,
                RoomDetailsResponseDTO.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(request.name(), getResponse.getBody().name());
        assertEquals(request.number(), getResponse.getBody().number());
        assertEquals(request.location(), getResponse.getBody().location());
        assertEquals(request.locationDescription(), getResponse.getBody().locationDescription());
        assertEquals(request.capacity(), getResponse.getBody().capacity());
        assertEquals(request.isActive(), getResponse.getBody().isActive());
        assertEquals(request.area(), getResponse.getBody().area());
    }

    @Test
    void testUpdateRoom() {
        RoomRequestDTO initialRequest = new RoomRequestDTO("Alter Raumname", "1", "Alte Adresse 1", "Es war einmal", 1000, true, 200, Set.of(), Set.of(), null);
        RoomDetailsResponseDTO created = testRestTemplate.postForObject(ROOMS_URL, initialRequest, RoomDetailsResponseDTO.class);

        RoomRequestDTO updateRequest = new RoomRequestDTO("Neuer Raumname", "2", "Neue Adresse 2", "Jetzt neu und hier", 1001, true, 202, Set.of(), Set.of(),
                null);

        ResponseEntity<RoomDetailsResponseDTO> updateResponse = testRestTemplate.exchange(
                ROOMS_URL + "/" + created.id(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                RoomDetailsResponseDTO.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals("Neuer Raumname", updateResponse.getBody().name());
        assertEquals(1001, updateResponse.getBody().capacity());
    }

    @Test
    void testDeleteRoom() {
        RoomRequestDTO request = new RoomRequestDTO("Löschbarer Raum", "0", "Memory Lane 1", "Nicht mehr auffindbar", 5, false, 10, Set.of(), Set.of(), null);
        RoomDetailsResponseDTO created = testRestTemplate.postForObject(ROOMS_URL, request, RoomDetailsResponseDTO.class);

        ResponseEntity<Void> deleteResponse = testRestTemplate.exchange(
                ROOMS_URL + "/" + created.id(),
                HttpMethod.DELETE,
                null,
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }

    @Test
    void testCreateRoomWithRelationships() {
        SeatingType seatingType = new SeatingType();
        seatingType.setName("Reihenbestuhlung");
        seatingType.setDescription("ALle Stühle in einer Reihe");
        seatingType.setActive(true);
        seatingType = seatingTypeRepository.save(seatingType);

        Equipment equipment = new Equipment();
        equipment.setName("Projektor");
        equipment.setDescription("Ein schöner Projektor");
        equipment.setActive(true);
        equipment = equipmentRepository.save(equipment);

        InternalPerson person = new InternalPerson();
        person.setFirstName("Max");
        person.setLastName("Mustermann");
        person.setEmail("max@muenchen.de");
        person.setOrganisationId("123e4567-e89b-12d3-a456-426614171000");
        person.setOrganisationUnit("DKL33");
        person.setRoleFunction("Chef");
        person = personRepository.save(person);

        SeatingCapacityRequestDTO seatingReq = new SeatingCapacityRequestDTO(seatingType.getId(), 15);

        RoomRequestDTO request = new RoomRequestDTO(
                "Raum mit allem", "101", "Hier 3", "ganz oben", 10000, true, 600,
                Set.of(seatingReq),
                Set.of(equipment.getId()),
                person.getId());

        ResponseEntity<RoomDetailsResponseDTO> response = testRestTemplate.postForEntity(
                ROOMS_URL, request, RoomDetailsResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        RoomDetailsResponseDTO body = response.getBody();
        assertNotNull(body);

        assertEquals(1, body.roomSeatingCapacities().size());
        assertEquals(seatingReq.capacity(), body.roomSeatingCapacities().iterator().next().capacity());
        assertEquals(seatingType.isActive(), body.roomSeatingCapacities().iterator().next().seatingType().isActive());
        assertEquals(seatingType.getName(), body.roomSeatingCapacities().iterator().next().seatingType().name());
        assertEquals(seatingType.getDescription(), body.roomSeatingCapacities().iterator().next().seatingType().description());
        assertEquals(1, body.equipment().size());
        assertEquals(equipment.getName(), body.equipment().iterator().next().name());
        assertEquals(equipment.getDescription(), body.equipment().iterator().next().description());
        assertEquals(equipment.isActive(), body.equipment().iterator().next().isActive());
        InternalPersonResponseDto internalPersonResponseDto = (InternalPersonResponseDto) body.contactPerson();
        assertEquals(person.getFirstName(), internalPersonResponseDto.firstName());
        assertEquals(person.getLastName(), internalPersonResponseDto.lastName());
        assertEquals(person.getEmail(), internalPersonResponseDto.email());
        assertEquals(person.getOrganisationId(), internalPersonResponseDto.organisationId());
        assertEquals(person.getOrganisationUnit(), internalPersonResponseDto.organisationUnit());
        assertEquals(person.getRoleFunction(), internalPersonResponseDto.roleFunction());
    }

    @Test
    void testUpdateRoomRelationships() {
        // 1. Given: A room and two different equipments
        Equipment eq1 = new Equipment();
        eq1.setName("Whiteboard");
        eq1.setDescription("Ein schönes weißes Brett.");
        eq1.setActive(true);
        eq1 = equipmentRepository.save(eq1);

        Equipment eq2 = new Equipment();
        eq2.setName("Webcam");
        eq2.setDescription("Eine schöne kleine Webcam.");
        eq2.setActive(true);
        eq2 = equipmentRepository.save(eq2);

        SeatingType seatingType1 = new SeatingType();
        seatingType1.setName("Reihenbestuhlung");
        seatingType1.setDescription("ALle Stühle in einer Reihe");
        seatingType1.setActive(true);
        seatingType1 = seatingTypeRepository.save(seatingType1);
        SeatingCapacityRequestDTO seatingReq1 = new SeatingCapacityRequestDTO(seatingType1.getId(), 111);

        SeatingType seatingType2 = new SeatingType();
        seatingType2.setName("Kreisbestuhlung");
        seatingType2.setDescription("ALle Stühle in einem Kreis");
        seatingType2.setActive(true);
        seatingType2 = seatingTypeRepository.save(seatingType2);
        SeatingCapacityRequestDTO seatingReq2 = new SeatingCapacityRequestDTO(seatingType2.getId(), 222);

        InternalPerson person1 = new InternalPerson();
        person1.setFirstName("Max");
        person1.setLastName("Mustermann");
        person1.setEmail("max@muenchen.de");
        person1.setOrganisationUnit("DKL33");
        person1.setOrganisationId("123e4567-e89b-12d3-a456-426614171001");
        person1.setRoleFunction("Chef");
        person1 = personRepository.save(person1);

        InternalPerson person2 = new InternalPerson();
        person2.setFirstName("Maxima");
        person2.setLastName("Mustermann");
        person2.setEmail("maxima@muenchen.de");
        person2.setOrganisationId("123e4567-e89b-12d3-a456-426614171002");
        person2.setOrganisationUnit("DKL34");
        person2.setRoleFunction("Chefin");
        person2 = personRepository.save(person2);

        RoomRequestDTO initialReq = new RoomRequestDTO("Raum 1", "1", "Straße 1", "Hier vorne links", 1000, true, 100, Set.of(seatingReq1), Set.of(eq1.getId()),
                person1.getId());
        RoomDetailsResponseDTO created = testRestTemplate.postForObject(ROOMS_URL, initialReq, RoomDetailsResponseDTO.class);

        RoomRequestDTO updateReq = new RoomRequestDTO("Raum 1.1", "2", "Straße 1b", "Dort hinten rechts", 2000, true, 200, Set.of(seatingReq2),
                Set.of(eq2.getId()), person2.getId());

        ResponseEntity<RoomDetailsResponseDTO> response = testRestTemplate.exchange(
                ROOMS_URL + "/" + created.id(),
                HttpMethod.PUT,
                new HttpEntity<>(updateReq),
                RoomDetailsResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().roomSeatingCapacities().size());
        assertEquals(seatingReq2.capacity(), response.getBody().roomSeatingCapacities().iterator().next().capacity());
        assertEquals(seatingType2.isActive(), response.getBody().roomSeatingCapacities().iterator().next().seatingType().isActive());
        assertEquals(seatingType2.getName(), response.getBody().roomSeatingCapacities().iterator().next().seatingType().name());
        assertEquals(seatingType2.getDescription(), response.getBody().roomSeatingCapacities().iterator().next().seatingType().description());
        assertEquals(1, response.getBody().equipment().size());
        assertEquals(1, response.getBody().equipment().size());
        assertEquals(eq2.getName(), response.getBody().equipment().iterator().next().name());
        assertEquals(eq2.getDescription(), response.getBody().equipment().iterator().next().description());
        assertEquals(eq2.isActive(), response.getBody().equipment().iterator().next().isActive());
        InternalPersonResponseDto internalPersonResponseDto = (InternalPersonResponseDto) response.getBody().contactPerson();
        assertEquals(person2.getFirstName(), internalPersonResponseDto.firstName());
        assertEquals(person2.getLastName(), internalPersonResponseDto.lastName());
        assertEquals(person2.getEmail(), internalPersonResponseDto.email());
        assertEquals(person2.getOrganisationId(), internalPersonResponseDto.organisationId());
        assertEquals(person2.getOrganisationUnit(), internalPersonResponseDto.organisationUnit());
        assertEquals(person2.getRoleFunction(), internalPersonResponseDto.roleFunction());
    }

    @Test
    void testCreateSeveralAndGetAllRooms() {
        roomRepository.deleteAll();
        final RoomRequestDTO request1 = new RoomRequestDTO(
                "Raum 1",
                "1",
                "Straße 1",
                "Ganz hinten links",
                2000,
                true,
                500,
                Set.of(),
                Set.of(),
                null);
        final RoomRequestDTO request2 = new RoomRequestDTO(
                "Raum 2",
                "2",
                "Straße 2",
                "Ganz hinten links und dann rechts",
                1000,
                true,
                400,
                Set.of(),
                Set.of(),
                null);
        final RoomRequestDTO request3 = new RoomRequestDTO(
                "Raum 3",
                "3",
                "Straße 3",
                "Ganz hinten links, dann rechts, dann links",
                200,
                true,
                50,
                Set.of(),
                Set.of(),
                null);

        ResponseEntity<RoomDetailsResponseDTO> createResponse1 = testRestTemplate.postForEntity(
                ROOMS_URL,
                request1,
                RoomDetailsResponseDTO.class);
        ResponseEntity<RoomDetailsResponseDTO> createResponse2 = testRestTemplate.postForEntity(
                ROOMS_URL,
                request2,
                RoomDetailsResponseDTO.class);
        ResponseEntity<RoomDetailsResponseDTO> createResponse3 = testRestTemplate.postForEntity(
                ROOMS_URL,
                request3,
                RoomDetailsResponseDTO.class);

        assertEquals(HttpStatus.CREATED, createResponse1.getStatusCode());
        assertEquals(HttpStatus.CREATED, createResponse2.getStatusCode());
        assertEquals(HttpStatus.CREATED, createResponse3.getStatusCode());
        assertNotNull(createResponse1.getBody());
        assertNotNull(createResponse2.getBody());
        assertNotNull(createResponse3.getBody());

        ResponseEntity<RoomListResponseDTO[]> getResponse = testRestTemplate.getForEntity(
                ROOMS_URL,
                RoomListResponseDTO[].class);

        List<RoomListResponseDTO> expectedRooms = List.of(
                new RoomListResponseDTO(createResponse1.getBody().id(), request1.name(), request1.number(), request1.isActive()),
                new RoomListResponseDTO(createResponse2.getBody().id(), request2.name(), request2.number(), request2.isActive()),
                new RoomListResponseDTO(createResponse3.getBody().id(), request3.name(), request3.number(), request3.isActive()));
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        assertNotNull(getResponse.getBody());
        assertEquals(expectedRooms, Arrays.asList(getResponse.getBody()));
    }

}
