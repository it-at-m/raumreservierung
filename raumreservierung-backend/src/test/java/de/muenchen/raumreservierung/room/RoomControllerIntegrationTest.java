package de.muenchen.raumreservierung.room;

import static de.muenchen.raumreservierung.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.raumreservierung.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muenchen.raumreservierung.MicroServiceApplication;
import de.muenchen.raumreservierung.TestConstants;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.equipment.EquipmentRepository;
import de.muenchen.raumreservierung.person.PersonRepository;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.person.dto.InternalPersonResponseDto;
import de.muenchen.raumreservierung.room.dto.RoomDetailsResponseDTO;
import de.muenchen.raumreservierung.room.dto.RoomListResponseDTO;
import de.muenchen.raumreservierung.room.dto.RoomRequestDTO;
import de.muenchen.raumreservierung.room.dto.SeatingCapacityRequestDTO;
import de.muenchen.raumreservierung.seating.SeatingRepository;
import de.muenchen.raumreservierung.seating.SeatingType;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

    @MockitoBean
    private RoleHierarchy roleHierarchy;

    @BeforeEach
    void setUp() {
        roomRepository.deleteAll();
        personRepository.deleteAll();
    }

    @Test
    void testCreateAndGetRoom() {
        final RoomRequestDTO request = createBaseRequest();
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
        final RoomRequestDTO request = createBaseRequest();
        RoomDetailsResponseDTO created = testRestTemplate.postForObject(ROOMS_URL, request, RoomDetailsResponseDTO.class);

        final RoomRequestDTO updateRequest = createBaseRequest2(true);

        ResponseEntity<RoomDetailsResponseDTO> updateResponse = testRestTemplate.exchange(
                ROOMS_URL + "/" + created.id(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                RoomDetailsResponseDTO.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals(updateRequest.name(), updateResponse.getBody().name());
        assertEquals(updateRequest.capacity(), updateResponse.getBody().capacity());
    }

    @Test
    void testDeleteRoom() {
        final RoomRequestDTO request = createBaseRequest2(false);
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
        SeatingType seatingType = createBaseSeatingType();

        Equipment equipment = createBaseEquipment();

        InternalPerson person = createBaseInternalPerson();

        SeatingCapacityRequestDTO seatingReq = new SeatingCapacityRequestDTO(seatingType.getId(), 15);

        RoomRequestDTO request = createBaseRequest(List.of(seatingReq), List.of(equipment.getId()), person.getId());

        ResponseEntity<RoomDetailsResponseDTO> response = testRestTemplate.postForEntity(
                ROOMS_URL, request, RoomDetailsResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        RoomDetailsResponseDTO body = response.getBody();
        assertNotNull(body);

        assertEquals(1, body.roomSeatingCapacities().size());
        assertEquals(seatingReq.capacity(), body.roomSeatingCapacities().getFirst().capacity());
        assertEquals(seatingType.isActive(), body.roomSeatingCapacities().getFirst().seatingType().isActive());
        assertEquals(seatingType.getName(), body.roomSeatingCapacities().getFirst().seatingType().name());
        assertEquals(seatingType.getDescription(), body.roomSeatingCapacities().getFirst().seatingType().description());
        assertEquals(1, body.equipment().size());
        assertEquals(equipment.getName(), body.equipment().getFirst().name());
        assertEquals(equipment.getDescription(), body.equipment().getFirst().description());
        assertEquals(equipment.isActive(), body.equipment().getFirst().isActive());
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
        Equipment eq1 = createBaseEquipment();
        Equipment eq2 = createBaseEquipment2();

        SeatingType seatingType1 = createBaseSeatingType();
        SeatingCapacityRequestDTO seatingReq1 = new SeatingCapacityRequestDTO(seatingType1.getId(), 111);

        SeatingType seatingType2 = createBaseSeatingType2();
        SeatingCapacityRequestDTO seatingReq2 = new SeatingCapacityRequestDTO(seatingType2.getId(), 222);

        InternalPerson person1 = createBaseInternalPerson();
        InternalPerson person2 = createBaseInternalPerson2();

        final RoomRequestDTO initialReq = createBaseRequest(List.of(seatingReq1), List.of(eq1.getId()), person1.getId());
        RoomDetailsResponseDTO created = testRestTemplate.postForObject(ROOMS_URL, initialReq, RoomDetailsResponseDTO.class);

        final RoomRequestDTO updateReq = createBaseRequest(List.of(seatingReq2), List.of(eq2.getId()), person2.getId());

        ResponseEntity<RoomDetailsResponseDTO> response = testRestTemplate.exchange(
                ROOMS_URL + "/" + created.id(),
                HttpMethod.PUT,
                new HttpEntity<>(updateReq),
                RoomDetailsResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().roomSeatingCapacities().size());
        assertEquals(seatingReq2.capacity(), response.getBody().roomSeatingCapacities().getFirst().capacity());
        assertEquals(seatingType2.isActive(), response.getBody().roomSeatingCapacities().getFirst().seatingType().isActive());
        assertEquals(seatingType2.getName(), response.getBody().roomSeatingCapacities().getFirst().seatingType().name());
        assertEquals(seatingType2.getDescription(), response.getBody().roomSeatingCapacities().getFirst().seatingType().description());
        assertEquals(1, response.getBody().equipment().size());
        assertEquals(1, response.getBody().equipment().size());
        assertEquals(eq2.getName(), response.getBody().equipment().getFirst().name());
        assertEquals(eq2.getDescription(), response.getBody().equipment().getFirst().description());
        assertEquals(eq2.isActive(), response.getBody().equipment().getFirst().isActive());
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
        final RoomRequestDTO request = createBaseRequest();
        final RoomRequestDTO request2 = createBaseRequest2(true);
        final RoomRequestDTO request3 = createBaseRequest3();
        ResponseEntity<RoomDetailsResponseDTO> createResponse1 = testRestTemplate.postForEntity(
                ROOMS_URL,
                request,
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
                new RoomListResponseDTO(createResponse1.getBody().id(), request.name(), request.number(), request.isActive()),
                new RoomListResponseDTO(createResponse2.getBody().id(), request2.name(), request2.number(), request2.isActive()),
                new RoomListResponseDTO(createResponse3.getBody().id(), request3.name(), request3.number(), request3.isActive()));
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        assertNotNull(getResponse.getBody());
        assertEquals(expectedRooms, Arrays.asList(getResponse.getBody()));
    }

    @Test
    void getRooms_shouldReturnOnlyActiveRooms() {
        roomRepository.deleteAll();
        final RoomRequestDTO request = createBaseRequest();
        final RoomRequestDTO request2Inactive = createBaseRequest2(false);
        ResponseEntity<RoomDetailsResponseDTO> createResponse1 = testRestTemplate.postForEntity(
                ROOMS_URL,
                request,
                RoomDetailsResponseDTO.class);
        ResponseEntity<RoomDetailsResponseDTO> createResponse2 = testRestTemplate.postForEntity(
                ROOMS_URL,
                request2Inactive,
                RoomDetailsResponseDTO.class);

        assertEquals(HttpStatus.CREATED, createResponse1.getStatusCode());
        assertEquals(HttpStatus.CREATED, createResponse2.getStatusCode());
        assertNotNull(createResponse1.getBody());
        assertNotNull(createResponse2.getBody());

        ResponseEntity<RoomListResponseDTO[]> getResponse = testRestTemplate.getForEntity(
                ROOMS_URL,
                RoomListResponseDTO[].class);

        List<RoomListResponseDTO> expectedRooms = List
                .of(new RoomListResponseDTO(createResponse1.getBody().id(), request.name(), request.number(), request.isActive()));
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        assertNotNull(getResponse.getBody());
        assertEquals(expectedRooms, Arrays.asList(getResponse.getBody()));
    }

    @Test
    void getRooms_givenOnlyActiveTrue_shouldReturnOnlyActiveRooms() {
        roomRepository.deleteAll();
        final RoomRequestDTO request = createBaseRequest();
        final RoomRequestDTO request2Inactive = createBaseRequest2(false);
        ResponseEntity<RoomDetailsResponseDTO> createResponse1 = testRestTemplate.postForEntity(
                ROOMS_URL,
                request,
                RoomDetailsResponseDTO.class);
        ResponseEntity<RoomDetailsResponseDTO> createResponse2 = testRestTemplate.postForEntity(
                ROOMS_URL,
                request2Inactive,
                RoomDetailsResponseDTO.class);

        assertEquals(HttpStatus.CREATED, createResponse1.getStatusCode());
        assertEquals(HttpStatus.CREATED, createResponse2.getStatusCode());
        assertNotNull(createResponse1.getBody());
        assertNotNull(createResponse2.getBody());

        ResponseEntity<RoomListResponseDTO[]> getResponse = testRestTemplate.getForEntity(
                ROOMS_URL + "?onlyActive=true",
                RoomListResponseDTO[].class);

        List<RoomListResponseDTO> expectedRooms = List
                .of(new RoomListResponseDTO(createResponse1.getBody().id(), request.name(), request.number(), request.isActive()));
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        assertNotNull(getResponse.getBody());
        assertEquals(expectedRooms, Arrays.asList(getResponse.getBody()));
    }

    @Test
    void getRooms_givenOnlyActiveFalse_shouldReturnAllRooms() {
        roomRepository.deleteAll();
        final RoomRequestDTO request = createBaseRequest();
        final RoomRequestDTO request2Inactive = createBaseRequest2(false);
        ResponseEntity<RoomDetailsResponseDTO> createResponse1 = testRestTemplate.postForEntity(
                ROOMS_URL,
                request,
                RoomDetailsResponseDTO.class);
        ResponseEntity<RoomDetailsResponseDTO> createResponse2 = testRestTemplate.postForEntity(
                ROOMS_URL,
                request2Inactive,
                RoomDetailsResponseDTO.class);

        assertEquals(HttpStatus.CREATED, createResponse1.getStatusCode());
        assertEquals(HttpStatus.CREATED, createResponse2.getStatusCode());
        assertNotNull(createResponse1.getBody());
        assertNotNull(createResponse2.getBody());

        ResponseEntity<RoomListResponseDTO[]> getResponse = testRestTemplate.getForEntity(
                ROOMS_URL + "?onlyActive=false",
                RoomListResponseDTO[].class);

        List<RoomListResponseDTO> expectedRooms = List.of(
                new RoomListResponseDTO(createResponse1.getBody().id(), request.name(), request.number(), request.isActive()),
                new RoomListResponseDTO(createResponse2.getBody().id(), request2Inactive.name(), request2Inactive.number(), request2Inactive.isActive()));
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        assertNotNull(getResponse.getBody());
        assertEquals(expectedRooms, Arrays.asList(getResponse.getBody()));
    }

    @Test
    void createRoom_shouldThrow_whenSameSeatingTypeInRoomSeatingCapacities() {
        SeatingType seatingType = createBaseSeatingType();
        SeatingCapacityRequestDTO seatingReq1 = new SeatingCapacityRequestDTO(seatingType.getId(), 111);
        SeatingCapacityRequestDTO seatingReq2 = new SeatingCapacityRequestDTO(seatingType.getId(), 222);

        final RoomRequestDTO request = createBaseRequest(List.of(seatingReq1, seatingReq2), null, null);

        ResponseEntity<RoomDetailsResponseDTO> createResponse = testRestTemplate.postForEntity(
                ROOMS_URL,
                request,
                RoomDetailsResponseDTO.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, createResponse.getStatusCode());
    }

    private RoomRequestDTO createBaseRequest() {
        return createBaseRequest(List.of(), List.of(), null);
    }

    private RoomRequestDTO createBaseRequest(List<SeatingCapacityRequestDTO> seatingReqs, List<UUID> equipmentUUIDs, UUID contactPersonId) {
        return new RoomRequestDTO(
                "Raum 1",
                "1",
                "Straße 1",
                "Ganz hinten links",
                2000,
                true,
                500,
                seatingReqs,
                equipmentUUIDs,
                contactPersonId);
    }

    private RoomRequestDTO createBaseRequest2(final boolean isActive) {
        return new RoomRequestDTO(
                "Raum 2",
                "2",
                "Straße 2",
                "Ganz hinten links und dann rechts",
                1000,
                isActive,
                400,
                List.of(),
                List.of(),
                null);
    }

    private RoomRequestDTO createBaseRequest3() {
        return new RoomRequestDTO(
                "Raum 3",
                "3",
                "Straße 3",
                "Ganz hinten links, dann rechts, dann links",
                200,
                true,
                50,
                List.of(),
                List.of(),
                null);
    }

    private Equipment createBaseEquipment() {
        Equipment eq1 = new Equipment();
        eq1.setName("Whiteboard");
        eq1.setDescription("Ein schönes weißes Brett.");
        eq1.setActive(true);
        return equipmentRepository.save(eq1);
    }

    private Equipment createBaseEquipment2() {
        Equipment eq2 = new Equipment();
        eq2.setName("Webcam");
        eq2.setDescription("Eine schöne kleine Webcam.");
        eq2.setActive(true);
        return equipmentRepository.save(eq2);
    }

    private SeatingType createBaseSeatingType() {
        SeatingType seatingType1 = new SeatingType();
        seatingType1.setName("Reihenbestuhlung");
        seatingType1.setDescription("ALle Stühle in einer Reihe");
        seatingType1.setActive(true);
        return seatingTypeRepository.save(seatingType1);
    }

    private SeatingType createBaseSeatingType2() {
        SeatingType seatingType2 = new SeatingType();
        seatingType2.setName("Kreisbestuhlung");
        seatingType2.setDescription("ALle Stühle in einem Kreis");
        seatingType2.setActive(true);
        return seatingTypeRepository.save(seatingType2);
    }

    private InternalPerson createBaseInternalPerson() {
        InternalPerson person1 = new InternalPerson();
        person1.setFirstName("Max");
        person1.setLastName("Mustermann");
        person1.setEmail("max@muenchen.de");
        person1.setOrganisationUnit("DKL33");
        person1.setOrganisationId("123e4567-e89b-12d3-a456-426614171001");
        person1.setRoleFunction("Chef");
        return personRepository.save(person1);
    }

    private InternalPerson createBaseInternalPerson2() {
        InternalPerson person2 = new InternalPerson();
        person2.setFirstName("Maxima");
        person2.setLastName("Mustermann");
        person2.setEmail("maxima@muenchen.de");
        person2.setOrganisationId("123e4567-e89b-12d3-a456-426614171002");
        person2.setOrganisationUnit("DKL34");
        person2.setRoleFunction("Chefin");
        return personRepository.save(person2);
    }

}
