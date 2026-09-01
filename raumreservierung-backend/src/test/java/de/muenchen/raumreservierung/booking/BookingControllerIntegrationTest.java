package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.TestConstants.SPRING_TEST_PROFILE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_ROOM_INACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_SEATINGTYPE_NOT_AVAILABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import de.muenchen.raumreservierung.TestConstants;
import de.muenchen.raumreservierung.appointment.dto.AppointmentDetailsResponseDTO;
import de.muenchen.raumreservierung.booking.dto.BookingDetailResponseDTO;
import de.muenchen.raumreservierung.booking.dto.BookingRequestDTO;
import de.muenchen.raumreservierung.person.PersonRepository;
import de.muenchen.raumreservierung.person.domain.ExternalPerson;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.room.Room;
import de.muenchen.raumreservierung.room.RoomRepository;
import de.muenchen.raumreservierung.room.RoomSeatingCapacity;
import de.muenchen.raumreservierung.seating.SeatingRepository;
import de.muenchen.raumreservierung.seating.SeatingType;
import de.muenchen.raumreservierung.security.Roles;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
public class BookingControllerIntegrationTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private static final String BOOKINGS_URL = "/bookings";
    private static final String APPOINTMENTS_URL = "/appointments";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private SeatingRepository seatingRepository;

    private InternalPerson mockPerson;
    private InternalPerson mockInternalPerson;
    private ExternalPerson mockExternalPerson;
    private Room mockRoom;
    private Room mockRoomInactive;
    private SeatingType mockSeatingType1;
    private SeatingType mockSeatingType2;
    private Booking mockBooking;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        personRepository.deleteAll();
        mockPerson = new InternalPerson();
        mockPerson.setOrganisationUnit("TEST_UNIT");
        mockPerson.setOrganisationId("000001");
        mockPerson.setEmail("TEST_EMAIL");
        mockPerson.setRoleFunction("anwender");
        mockPerson = personRepository.save(mockPerson);

        mockInternalPerson = new InternalPerson();
        mockInternalPerson.setOrganisationUnit("INTERNAL_UNIT");
        mockInternalPerson.setOrganisationId("000004");
        mockInternalPerson.setEmail("INTERNAL_EMAIL");
        mockInternalPerson.setRoleFunction("anwender");
        mockInternalPerson = personRepository.save(mockInternalPerson);

        mockExternalPerson = new ExternalPerson();
        mockExternalPerson.setEmail("EXTERNAL_EMAIL");
        mockExternalPerson.setLastModified(LocalDate.now());
        mockExternalPerson = personRepository.save(mockExternalPerson);

        mockRoom = new Room();
        mockRoom.setName("TEST_ROOM_NAME");
        mockRoom.setNumber("100");
        mockRoom.setActive(true);
        mockRoom.setCapacity(100);
        mockRoom = roomRepository.save(mockRoom);

        mockRoomInactive = new Room();
        mockRoomInactive.setName("TEST_ROOM_NAME_2");
        mockRoomInactive.setNumber("200");
        mockRoomInactive.setActive(false);
        mockRoomInactive.setCapacity(200);
        mockRoomInactive = roomRepository.save(mockRoomInactive);

        mockSeatingType1 = new SeatingType();
        mockSeatingType1.setName("TEST_SEATING");
        mockSeatingType1.setActive(true);
        mockSeatingType1 = seatingRepository.save(mockSeatingType1);

        mockSeatingType2 = new SeatingType();
        mockSeatingType2.setName("TEST_SEATING_2");
        mockSeatingType2.setActive(true);
        mockSeatingType2 = seatingRepository.save(mockSeatingType2);

        RoomSeatingCapacity mockRoomSeatingCapacity = new RoomSeatingCapacity();
        mockRoomSeatingCapacity.setRoom(mockRoom);
        mockRoomSeatingCapacity.setSeatingType(mockSeatingType1);
        mockRoomSeatingCapacity.setCapacity(5);

        mockRoom.setRoomSeatingCapacities(Set.of(mockRoomSeatingCapacity));
        mockRoom = roomRepository.save(mockRoom);

        mockBooking = new Booking();
        mockBooking.setTitle("TEST_BOOKING_TITLE");
        mockBooking.setBookedBy(mockPerson);
        mockBooking.setOrganisationUnit("TEST_UNIT");
        mockBooking.setRoom(mockRoom);
        mockBooking.setStatus(BookingStatus.ORGANIZER_APPROVED);
        mockBooking.setBookingType(BookingType.DEFAULT);
        mockBooking = bookingRepository.save(mockBooking);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("createBookingCreatedScenarios")
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_ReturnsCreated(String testName,
            Supplier<UUID> roomIdSupplier,
            Supplier<UUID> seatingTypeIdSupplier,
            int seatCount) throws Exception {

        BookingRequestDTO request = getBookingRequestDTOWithRoomAndSeatingAndParticipantCount(
                roomIdSupplier.get(), seatingTypeIdSupplier.get(), seatCount);

        mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    Stream<Arguments> createBookingCreatedScenarios() {
        return Stream.of(
                Arguments.of("no room, no seating type",
                        (Supplier<UUID>) () -> null, (Supplier<UUID>) () -> null, 100),

                Arguments.of("room has requested seating type",
                        (Supplier<UUID>) () -> mockRoom.getId(), (Supplier<UUID>) () -> mockSeatingType1.getId(), 4),

                Arguments.of("room, but no seating type",
                        (Supplier<UUID>) () -> mockRoom.getId(), (Supplier<UUID>) () -> null, 4));
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("createBookingBadRequestScenarios")
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_ReturnsBadRequest(String testName,
            Supplier<UUID> roomIdSupplier,
            Supplier<UUID> seatingTypeIdSupplier,
            int seatCount,
            String expectedReason) throws Exception {

        BookingRequestDTO request = getBookingRequestDTOWithRoomAndSeatingAndParticipantCount(
                roomIdSupplier.get(), seatingTypeIdSupplier.get(), seatCount);

        mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(expectedReason));
    }

    Stream<Arguments> createBookingBadRequestScenarios() {
        return Stream.of(
                Arguments.of("room has not the requested seating type",
                        (Supplier<UUID>) () -> mockRoom.getId(), (Supplier<UUID>) () -> mockSeatingType2.getId(),
                        100, MSG_SEATINGTYPE_NOT_AVAILABLE),

                Arguments.of("room inactive",
                        (Supplier<UUID>) () -> mockRoomInactive.getId(), (Supplier<UUID>) () -> null,
                        100, MSG_ROOM_INACTIVE),

                Arguments.of("no room, but seating type",
                        (Supplier<UUID>) () -> null, (Supplier<UUID>) () -> mockSeatingType1.getId(),
                        100, MSG_SEATINGTYPE_NOT_AVAILABLE));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_ReturnsCreated_WhenAuthenticatedAndNoRRule() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();
        BookingRequestDTO request = getBookingRequestDTOWithRruleAndBookedFor(now, null, mockPerson.getId());

        mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void updateBooking_ReturnsCreated_WhenRoomActive() throws Exception {
        BookingRequestDTO request = getBookingRequestDTOWithRoomAndSeatingAndParticipantCount(mockRoom.getId(), null, 100);
        mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void updateBooking_ReturnsBadRequest_WhenRoomInactive() throws Exception {
        BookingRequestDTO request = getBookingRequestDTOWithRoomAndSeatingAndParticipantCount(mockRoomInactive.getId(), null, 100);

        mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(MSG_ROOM_INACTIVE));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void deleteBooking_ReturnsNoContent_WhenDeleted() throws Exception {
        mockMvc.perform(delete(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void getBooking_ReturnsNoContent_WhenDeleted() throws Exception {
        mockMvc.perform(get(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.LESEBERECHTIGT })
    void createBooking_ReturnsCreated_WhenAuthenticatedAndRRules(String rrule, int expectedSize, List<OffsetDateTime> expectedDates) throws Exception {
        OffsetDateTime date = OffsetDateTime.of(2026, 3, 2, 13, 45, 0, 0, ZoneOffset.ofHours(2));
        BookingRequestDTO request = getBookingRequestDTOWithRruleAndBookedFor(date, rrule, mockPerson.getId());

        String responseJson = mockMvc.perform(post(BOOKINGS_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        OffsetDateTime start = OffsetDateTime.of(2026, 3, 1, 0, 0, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC);
        OffsetDateTime end = OffsetDateTime.of(2027, 2, 2, 0, 0, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC);
        String appointmentResponseJson = mockMvc.perform(get(APPOINTMENTS_URL).with(csrf())
                .param("bookingId", responseBody.id().toString())
                .param("startDate", start.toString())
                .param("endDate", end.toString())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String contentJson = JsonPath.read(appointmentResponseJson, "$.content").toString();
        List<AppointmentDetailsResponseDTO> appointments = objectMapper.readValue(
                contentJson,
                new TypeReference<>() {
                });

        assertThat(appointments).isNotEmpty();
        assertThat(appointments)
                .hasSize(expectedSize)
                .extracting(r -> r.schedule().occupancyStart())
                .containsExactlyInAnyOrderElementsOf(expectedDates);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                Arguments.of("", 1, List.of(OffsetDateTime.of(2026, 3, 2, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC))),
                Arguments.of("RRULE:FREQ=DAILY;COUNT=3", 3, List.of(
                        OffsetDateTime.of(2026, 3, 2, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 3, 3, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 3, 4, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC))),
                Arguments.of("RRULE:FREQ=MONTHLY;COUNT=2", 2, List.of(
                        OffsetDateTime.of(2026, 3, 2, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 4, 2, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC))),
                Arguments.of("RRULE:FREQ=WEEKLY;COUNT=5", 5, List.of(
                        OffsetDateTime.of(2026, 3, 2, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 3, 9, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 3, 16, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 3, 23, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 3, 30, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC))),
                Arguments.of("RRULE:FREQ=WEEKLY;BYDAY=TU,WE;INTERVAL=2;COUNT=4", 4, List.of(
                        OffsetDateTime.of(2026, 3, 3, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 3, 4, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 3, 17, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 3, 18, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC))),
                Arguments.of("RRULE:FREQ=MONTHLY;BYDAY=2WE;COUNT=3", 3, List.of(
                        OffsetDateTime.of(2026, 3, 11, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 4, 8, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 5, 13, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC))),
                Arguments.of("RRULE:FREQ=MONTHLY;INTERVAL=2;BYMONTHDAY=15;COUNT=4", 4, List.of(
                        OffsetDateTime.of(2026, 3, 15, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 5, 15, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 7, 15, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 9, 15, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC))),
                Arguments.of("RRULE:FREQ=WEEKLY;INTERVAL=6", 9, List.of(
                        OffsetDateTime.of(2026, 3, 2, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 4, 13, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 5, 25, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 7, 6, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 8, 17, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 9, 28, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 11, 9, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 12, 21, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2027, 2, 1, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC))));
    }

    private enum BookedForScenario {
        INTERNAL_PERSON,
        MISSING,
        EXTERNAL_PERSON
    }

    @ParameterizedTest
    @EnumSource(BookedForScenario.class)
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_AsAnwender_ShouldSetBookedForCorrectly(BookedForScenario scenario) throws Exception {
        UUID bookedForId = switch (scenario) {
        case MISSING -> null;
        case INTERNAL_PERSON -> mockInternalPerson.getId();
        case EXTERNAL_PERSON -> mockExternalPerson.getId();
        };

        UUID expectedBookedForId = switch (scenario) {
        case MISSING -> mockPerson.getId();
        case INTERNAL_PERSON -> mockInternalPerson.getId();
        case EXTERNAL_PERSON -> mockExternalPerson.getId();
        };

        OffsetDateTime now = OffsetDateTime.now();
        BookingRequestDTO request = getBookingRequestDTOWithRruleAndBookedFor(now, null, bookedForId);

        String responseJson = mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.bookedBy()).isNotNull();
        assertThat(responseBody.bookedFor()).isNotNull();

        assertThat(responseBody.bookedBy().id()).isEqualTo(mockPerson.getId());
        assertThat(responseBody.bookedFor().id()).isEqualTo(expectedBookedForId);
        assertThat(responseBody.organisationUnit()).isEqualTo(mockPerson.getOrganisationUnit());

        if (scenario == BookedForScenario.MISSING) {
            assertThat(responseBody.bookedFor()).isEqualTo(responseBody.bookedBy());
        } else {
            assertThat(responseBody.bookedFor().id()).isNotEqualTo(responseBody.bookedBy().id());
        }
    }

    @ParameterizedTest
    @EnumSource(BookedForScenario.class)
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void createBooking_AsRaumAdmin_ShouldSetBookedByCorrectly(BookedForScenario scenario) throws Exception {
        UUID bookedForId = switch (scenario) {
        case INTERNAL_PERSON -> mockInternalPerson.getId();
        case MISSING -> null;
        case EXTERNAL_PERSON -> mockExternalPerson.getId();
        };

        UUID expectedBookedForId = switch (scenario) {
        case INTERNAL_PERSON -> mockInternalPerson.getId();
        case MISSING -> mockPerson.getId();
        case EXTERNAL_PERSON -> mockExternalPerson.getId();
        };

        UUID expectedBookedById = switch (scenario) {
        case INTERNAL_PERSON -> mockInternalPerson.getId();
        case EXTERNAL_PERSON, MISSING -> mockPerson.getId();
        };

        String expectedOrganisationUnit = switch (scenario) {
        case INTERNAL_PERSON -> mockInternalPerson.getOrganisationUnit();
        case EXTERNAL_PERSON, MISSING -> mockPerson.getOrganisationUnit();
        };

        BookingRequestDTO request = getBookingRequestDTOWithRruleAndBookedFor(OffsetDateTime.now(), null, bookedForId);

        String responseJson = mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody.bookedFor().id()).as("bookedFor id").isEqualTo(expectedBookedForId);
        assertThat(responseBody.bookedBy().id()).as("bookedBy id").isEqualTo(expectedBookedById);
        assertThat(responseBody.organisationUnit()).as("organisationUnit").isEqualTo(expectedOrganisationUnit);
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void updateBooking_ShouldReturnForbiddenOrUnauthorized_WhenUserHasNoAuthority() throws Exception {
        InternalPerson foreignOwner = new InternalPerson();
        foreignOwner.setOrganisationId("000003");
        foreignOwner.setOrganisationUnit("FOREIGN_UNIT");
        foreignOwner.setEmail("FOREIGN_EMAIL");
        foreignOwner.setRoleFunction("anwender");
        foreignOwner = personRepository.save(foreignOwner);

        Booking existingBooking = getExistingBooking(foreignOwner, BookingType.DEFAULT);
        Booking saved = bookingRepository.save(existingBooking);

        BookingRequestDTO updates = getBookingRequestDTOWithRruleAndBookedFor(OffsetDateTime.now(), null, null);

        mockMvc.perform(put(BOOKINGS_URL + "/" + saved.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.LESEBERECHTIGT })
    void createBooking_AsLeseberechtigt_shouldNullifyInternalNotesOnCreateWhenRoleIsLeseberechtigt() throws Exception {
        BookingRequestDTO requestDto = getBookingRequestDTOWithRoomAndSeatingAndParticipantCount(null, null, 100);

        String responseContent = mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookingDetailResponseDTO responseDto = objectMapper.readValue(responseContent, BookingDetailResponseDTO.class);

        assertThat(responseDto.internalNotes()).isNull();
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.LESEBERECHTIGT })
    void updateBooking_AsLeseberechtigt_shouldNullifyInternalNotesOnUpdate() throws Exception {
        Booking existingBooking = getExistingBooking(mockPerson, BookingType.DEFAULT);
        Booking saved = bookingRepository.save(existingBooking);

        BookingRequestDTO updateDTO = getBookingRequestDTOWithRoomAndSeatingAndParticipantCount(null, null, 100);

        String responseContent = mockMvc.perform(put(BOOKINGS_URL + "/" + saved.getId())
                .with(csrf())
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookingDetailResponseDTO responseDto = objectMapper.readValue(responseContent, BookingDetailResponseDTO.class);

        assertThat(responseDto.internalNotes()).isNull();
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.TERMIN_ORGANISATOR })
    void createBooking_AsLeseberechtigt_shouldNotNullifyInternalNotesOnCreateWhenRoleIsTerminOrganisator() throws Exception {
        BookingRequestDTO requestDto = getBookingRequestDTOWithRoomAndSeatingAndParticipantCount(null, null, 100);

        String responseContent = mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookingDetailResponseDTO responseDto = objectMapper.readValue(responseContent, BookingDetailResponseDTO.class);

        assertThat(responseDto.internalNotes()).isEqualTo(requestDto.internalNotes());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.TERMIN_ORGANISATOR })
    void updateBooking_AsTerminOrganisator_shouldNotNullifyInternalNotesOnUpdate() throws Exception {
        Booking existingBooking = getExistingBooking(mockPerson, BookingType.DEFAULT);
        Booking saved = bookingRepository.save(existingBooking);

        BookingRequestDTO updateDTO = getBookingRequestDTOWithRoomAndSeatingAndParticipantCount(null, null, 100);

        String responseContent = mockMvc.perform(put(BOOKINGS_URL + "/" + saved.getId())
                .with(csrf())
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookingDetailResponseDTO responseDto = objectMapper.readValue(responseContent, BookingDetailResponseDTO.class);

        assertThat(responseDto.internalNotes()).isEqualTo(updateDTO.internalNotes());
    }

    @ParameterizedTest
    @ValueSource(ints = { 100000, -1 })
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_shouldBeInvalid_WhenParticipantCountOutOfRange(int participantCount) throws Exception {
        BookingRequestDTO updateDTO = getBookingRequestDTOWithRoomAndSeatingAndParticipantCount(null, null, participantCount);

        mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @EnumSource(BookingType.class)
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.TERMIN_ORGANISATOR })
    void createBooking_AsTerminorganisator_shouldKeepRequestedTypeOnCreate(BookingType bookingType) throws Exception {
        BookingRequestDTO requestDto = getBookingRequestDTOWithRoomAndSeatingAndBookingType(bookingType);

        String responseContent = mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookingDetailResponseDTO responseDto = objectMapper.readValue(responseContent, BookingDetailResponseDTO.class);

        assertThat(responseDto.bookingType()).isEqualTo(bookingType);
    }

    @ParameterizedTest
    @EnumSource(value = BookingType.class, names = { "FREE", "SERVICE" })
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.TERMIN_ORGANISATOR })
    void updateBooking_AsTerminorganisator_shouldSetRequestedTypeOnUpdateFromNormal(BookingType existingType) throws Exception {
        Booking existingBooking = getExistingBooking(mockPerson, existingType);
        Booking saved = bookingRepository.save(existingBooking);

        BookingRequestDTO updateDTO = getBookingRequestDTOWithRoomAndSeatingAndBookingType(BookingType.DEFAULT);

        String responseContent = mockMvc.perform(put(BOOKINGS_URL + "/" + saved.getId())
                .with(csrf())
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookingDetailResponseDTO responseDto = objectMapper.readValue(responseContent, BookingDetailResponseDTO.class);

        assertThat(responseDto.bookingType()).isEqualTo(updateDTO.bookingType());
    }

    @ParameterizedTest
    @EnumSource(BookingType.class)
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.LESEBERECHTIGT })
    void createBooking_AsLeseberechtigt_shouldAlwaysHaveNormalTypeOnCreate(BookingType requestedType) throws Exception {
        BookingRequestDTO requestDto = getBookingRequestDTOWithRoomAndSeatingAndBookingType(requestedType);

        String responseContent = mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookingDetailResponseDTO responseDto = objectMapper.readValue(responseContent, BookingDetailResponseDTO.class);

        assertThat(responseDto.bookingType()).isEqualTo(BookingType.DEFAULT);
    }

    @ParameterizedTest
    @MethodSource("provideLeseberechtigtUpdateTypeData")
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.LESEBERECHTIGT })
    void updateBooking_AsLeseberechtigt_shouldKeepExistingTypeRegardlessOfRequest(BookingType existingType, BookingType requestedType) throws Exception {
        Booking existingBooking = getExistingBooking(mockPerson, existingType);
        Booking saved = bookingRepository.save(existingBooking);

        BookingRequestDTO updateDTO = getBookingRequestDTOWithRoomAndSeatingAndBookingType(requestedType);

        String responseContent = mockMvc.perform(put(BOOKINGS_URL + "/" + saved.getId())
                .with(csrf())
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookingDetailResponseDTO responseDto = objectMapper.readValue(responseContent, BookingDetailResponseDTO.class);

        assertThat(responseDto.bookingType()).isEqualTo(existingType);
    }

    private static Stream<Arguments> provideLeseberechtigtUpdateTypeData() {
        return Stream.of(
                Arguments.of(BookingType.DEFAULT, BookingType.FREE),
                Arguments.of(BookingType.FREE, BookingType.SERVICE));
    }

    private Booking getExistingBooking(InternalPerson person, BookingType bookingType) {
        OffsetDateTime now = OffsetDateTime.now();
        Booking existingBooking = new Booking();
        existingBooking.setBookedBy(person);
        existingBooking.setBookedFor(person);
        existingBooking.setOrganisationUnit(person.getOrganisationUnit());
        existingBooking.setTitle("TEST_TITLE");
        existingBooking.setInternalNotes("secret note not to overwrite and not to read by leseberechtigt");
        existingBooking.setSchedule(new ScheduleTemplate(
                now,
                now.plusHours(2),
                now.plusMinutes(15),
                now.plusHours(1).plusMinutes(30)));
        existingBooking.setBookingType(bookingType);
        existingBooking.setStatus(BookingStatus.ORGANIZER_APPROVED);
        return existingBooking;
    }

    private BookingRequestDTO getBookingRequestDTO(
            UUID roomId,
            UUID seatingTypeId,
            UUID bookedForId,
            int participantCount,
            String note,
            String recurringRule,
            BookingType bookingType,
            OffsetDateTime start) {

        ScheduleTemplate schedule = new ScheduleTemplate(
                start,
                start.plusHours(2),
                start.plusMinutes(15),
                start.plusHours(1).plusMinutes(30));

        return new BookingRequestDTO(
                "Test",
                participantCount,
                null,
                false,
                note,
                "no notes necessary",
                recurringRule,
                roomId,
                schedule,
                bookedForId,
                seatingTypeId,
                BookingStatus.ORGANIZER_APPROVED,
                null,
                bookingType);
    }

    private BookingRequestDTO getBookingRequestDTOWithRoomAndSeatingAndBookingType(BookingType bookingType) {
        return getBookingRequestDTO(null, null, mockPerson.getId(), 100,
                "secret note", null, bookingType, OffsetDateTime.now());
    }

    private BookingRequestDTO getBookingRequestDTOWithRoomAndSeatingAndParticipantCount(UUID roomId, UUID seatingTypeId, int participantCount) {
        return getBookingRequestDTO(roomId, seatingTypeId, mockPerson.getId(), participantCount,
                "secret note", null, BookingType.DEFAULT, OffsetDateTime.now());
    }

    private BookingRequestDTO getBookingRequestDTOWithRruleAndBookedFor(
            OffsetDateTime now, String recurringRule, UUID bookedForId) {
        return getBookingRequestDTO(null, null, bookedForId, 100,
                "please clean", recurringRule, BookingType.DEFAULT, now);
    }
}
