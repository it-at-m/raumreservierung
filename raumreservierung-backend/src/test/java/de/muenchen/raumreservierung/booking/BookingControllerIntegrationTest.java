package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.TestConstants.SPRING_TEST_PROFILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

        mockRoom = new Room();
        mockRoom.setName("TEST_ROOM_NAME");
        mockRoom.setNumber("100");
        mockRoom.setActive(true);
        mockRoom = roomRepository.save(mockRoom);

        mockRoomInactive = new Room();
        mockRoomInactive.setName("TEST_ROOM_NAME_2");
        mockRoomInactive.setNumber("200");
        mockRoomInactive.setActive(false);
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
        roomRepository.save(mockRoom);

        mockBooking = new Booking();
        mockBooking.setTitle("TEST_BOOKING_TITLE");
        mockBooking.setBookedBy(mockPerson);
        mockBooking.setOrganisationUnit("TEST_UNIT");
        mockBooking.setRoom(mockRoom);
        mockBooking = bookingRepository.save(mockBooking);
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
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_ReturnsCreated_WhenNoRoomAndNoSeatingType() throws Exception {
        BookingRequestDTO request = getBookingRequestDTOWithRoomAndSeating(null, null);

        mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_ReturnsCreated_WhenRoomHasRequestedSeatingType() throws Exception {
        BookingRequestDTO request = getBookingRequestDTOWithRoomAndSeating(mockRoom.getId(), mockSeatingType1.getId());

        mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_ReturnsCreated_WhenNoSeatingTypeChosen() throws Exception {
        BookingRequestDTO request = getBookingRequestDTOWithRoomAndSeating(mockRoom.getId(), mockSeatingType1.getId());

        mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_ReturnsBadRequest_WhenRoomHasNotRequestedSeatingType() throws Exception {
        BookingRequestDTO request = getBookingRequestDTOWithRoomAndSeating(mockRoom.getId(), mockSeatingType2.getId());

        mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_ReturnsCreated_WhenRoomActive() throws Exception {
        BookingRequestDTO request = getBookingRequestDTOWithRoomAndSeating(mockRoom.getId(), null);

        mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_ReturnsBadRequest_WhenRoomInactive() throws Exception {
        BookingRequestDTO request = getBookingRequestDTOWithRoomAndSeating(mockRoomInactive.getId(), null);

        mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void updateBooking_ReturnsCreated_WhenRoomActive() throws Exception {
        BookingRequestDTO request = getBookingRequestDTOWithRoomAndSeating(mockRoom.getId(), null);
        mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_ReturnsBadRequest_WhenNoRoomChosenButSeatingType() throws Exception {
        BookingRequestDTO request = getBookingRequestDTOWithRoomAndSeating(null, mockSeatingType1.getId());

        mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
        mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void updateBooking_ReturnsBadRequest_WhenRoomInactive() throws Exception {
        BookingRequestDTO request = getBookingRequestDTOWithRoomAndSeating(mockRoomInactive.getId(), null);

        mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
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

    private BookingRequestDTO getBookingRequestDTOWithRruleAndBookedFor(OffsetDateTime now, String recurringRule, UUID bookedForId) {
        ScheduleTemplate schedule = new ScheduleTemplate(
                now,
                now.plusHours(2),
                now.plusMinutes(15),
                now.plusHours(1).plusMinutes(30));
        return new BookingRequestDTO(
                "Test",
                100,
                null,
                false,
                "please clean",
                "no notes necessary",
                recurringRule,
                null,
                schedule,
                bookedForId,
                null);
    }

    private BookingRequestDTO getBookingRequestDTOWithRoomAndSeating(
            UUID roomId,
            UUID seatingTypeId) {

        ScheduleTemplate schedule = new ScheduleTemplate(
                OffsetDateTime.now(),
                OffsetDateTime.now().plusHours(2),
                OffsetDateTime.now().plusMinutes(15),
                OffsetDateTime.now().plusHours(1).plusMinutes(30));

        return new BookingRequestDTO(
                "Test",
                100,
                null,
                false,
                "secret note",
                "no notes necessary",
                null,
                roomId,
                schedule,
                mockPerson.getId(),
                seatingTypeId);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                Arguments.of("", 1, List.of(OffsetDateTime.of(2026, 3, 2, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC))),
                Arguments.of("FREQ=DAILY;COUNT=3", 3, List.of(
                        OffsetDateTime.of(2026, 3, 2, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 3, 3, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 3, 4, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC))),
                Arguments.of("FREQ=MONTHLY;COUNT=2", 2, List.of(
                        OffsetDateTime.of(2026, 3, 2, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 4, 2, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC))),
                Arguments.of("FREQ=WEEKLY;COUNT=5", 5, List.of(
                        OffsetDateTime.of(2026, 3, 2, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 3, 9, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 3, 16, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 3, 23, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 3, 30, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC))),
                Arguments.of("FREQ=WEEKLY;BYDAY=TU,WE;INTERVAL=2;COUNT=4", 4, List.of(
                        OffsetDateTime.of(2026, 3, 3, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 3, 4, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 3, 17, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 3, 18, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC))),
                Arguments.of("FREQ=MONTHLY;BYDAY=2WE;COUNT=3", 3, List.of(
                        OffsetDateTime.of(2026, 3, 11, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 4, 8, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 5, 13, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC))),
                Arguments.of("FREQ=MONTHLY;INTERVAL=2;BYMONTHDAY=15;COUNT=4", 4, List.of(
                        OffsetDateTime.of(2026, 3, 15, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 5, 15, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 7, 15, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 9, 15, 13, 45, 0, 0, ZoneOffset.ofHours(2)).withOffsetSameInstant(ZoneOffset.UTC))),
                Arguments.of("FREQ=WEEKLY;INTERVAL=6", 9, List.of(
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

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_ShouldFallbackToCurrentPerson_WhenBookedForIsMissing() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();
        BookingRequestDTO request = getBookingRequestDTOWithRruleAndBookedFor(now, null, null);

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
        assertThat(responseBody.bookedFor())
                .isEqualTo(responseBody.bookedBy());
        assertThat(responseBody.bookedBy().id()).isEqualTo(mockPerson.getId());
        assertThat(responseBody.organisationUnit()).isEqualTo(mockPerson.getOrganisationUnit());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_ShouldKeepRequestedPerson_WhenBookedForIsProvided() throws Exception {
        InternalPerson bookedFor = new InternalPerson();
        bookedFor.setOrganisationId("0000002");
        bookedFor.setOrganisationUnit("TEST_UNIT2");
        bookedFor.setEmail("TEST_EMAIL2");
        bookedFor.setRoleFunction("anwender");

        bookedFor = personRepository.save(bookedFor);
        UUID bookedForId = bookedFor.getId();

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
        assertThat(responseBody.bookedFor().id())
                .isNotEqualTo(responseBody.bookedBy().id());
        assertThat(responseBody.bookedFor().id()).isEqualTo(bookedForId);
        assertThat(responseBody.bookedBy().id()).isEqualTo(mockPerson.getId());
        assertThat(responseBody.organisationUnit()).isEqualTo(mockPerson.getOrganisationUnit());
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

        OffsetDateTime now = OffsetDateTime.now();
        Booking existingBooking = new Booking();
        existingBooking.setBookedBy(foreignOwner);
        existingBooking.setBookedFor(foreignOwner);
        existingBooking.setOrganisationUnit(foreignOwner.getOrganisationUnit());
        existingBooking.setTitle("TEST_TITLE");
        existingBooking.setSchedule(new ScheduleTemplate(
                now,
                now.plusHours(2),
                now.plusMinutes(15),
                now.plusHours(1).plusMinutes(30)));
        Booking saved = bookingRepository.save(existingBooking);

        BookingRequestDTO updates = getBookingRequestDTOWithRruleAndBookedFor(OffsetDateTime.now(), null, null);

        mockMvc.perform(put(BOOKINGS_URL + "/" + saved.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void createBooking_AsRaumAdmin_ShouldSetBookedByToBookedFor_WhenBookedForIsInternalPerson() throws Exception {
        InternalPerson internalPerson = new InternalPerson();
        internalPerson.setOrganisationId("000004");
        internalPerson.setOrganisationUnit("INTERNAL_UNIT");
        internalPerson.setEmail("INTERNAL_EMAIL");
        internalPerson.setRoleFunction("anwender");
        internalPerson = personRepository.save(internalPerson);
        UUID internalPersonId = internalPerson.getId();

        BookingRequestDTO request = getBookingRequestDTOWithRruleAndBookedFor(OffsetDateTime.now(), null, internalPersonId);

        String responseJson = mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody.bookedFor().id()).isEqualTo(internalPersonId);
        assertThat(responseBody.bookedBy().id()).isEqualTo(internalPersonId);
        assertThat(responseBody.organisationUnit()).isEqualTo(mockPerson.getOrganisationUnit());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void createBooking_AsRaumAdmin_ShouldSetBookedByToBookedFor_WhenBookedForIsMissing() throws Exception {
        BookingRequestDTO request = getBookingRequestDTOWithRruleAndBookedFor(OffsetDateTime.now(), null, null);

        String responseJson = mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody.bookedFor().id()).isEqualTo(mockPerson.getId());
        assertThat(responseBody.bookedBy().id()).isEqualTo(mockPerson.getId());
        assertThat(responseBody.organisationUnit()).isEqualTo(mockPerson.getOrganisationUnit());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void createBooking_AsRaumAdmin_ShouldSetBookedByToCurrentPerson_WhenBookedForIsExternalPerson() throws Exception {
        ExternalPerson externalPerson = new ExternalPerson();
        externalPerson.setEmail("EXTERNAL_EMAIL");
        externalPerson.setLastModified(LocalDate.now());
        externalPerson = personRepository.save(externalPerson);
        UUID externalPersonId = externalPerson.getId();

        BookingRequestDTO request = getBookingRequestDTOWithRruleAndBookedFor(OffsetDateTime.now(), null, externalPersonId);

        String responseJson = mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody.bookedFor().id()).isEqualTo(externalPersonId);
        assertThat(responseBody.bookedBy().id()).isEqualTo(mockPerson.getId());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.LESEBERECHTIGT })
    void createBooking_AsLeseberechtigt_shouldNullifyInternalNotesOnCreateWhenRoleIsLeseberechtigt() throws Exception {
        BookingRequestDTO requestDto = getBookingRequestDTOWithRoomAndSeating(null, null);

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
    void updateBooking_AsLeseberechtigt_shouldNullifyInternalNotesOnUpdateWhenRoleIsLeseberechtigt() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();
        Booking existingBooking = new Booking();
        existingBooking.setBookedBy(mockPerson);
        existingBooking.setBookedFor(mockPerson);
        existingBooking.setInternalNotes("secret note not to overwrite and not to read by leseberechtigt");
        existingBooking.setOrganisationUnit(mockPerson.getOrganisationUnit());
        existingBooking.setTitle("TEST_TITLE");
        existingBooking.setSchedule(new ScheduleTemplate(
                now,
                now.plusHours(2),
                now.plusMinutes(15),
                now.plusHours(1).plusMinutes(30)));
        Booking saved = bookingRepository.save(existingBooking);

        BookingRequestDTO updateDTO = getBookingRequestDTOWithRoomAndSeating(null, null);

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
        BookingRequestDTO requestDto = getBookingRequestDTOWithRoomAndSeating(null, null);

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
    void updateBooking_AsLeseberechtigt_shouldNullifyInternalNotesOnUpdateWhenRoleIsTerminOrganisator() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();
        Booking existingBooking = new Booking();
        existingBooking.setBookedBy(mockPerson);
        existingBooking.setBookedFor(mockPerson);
        existingBooking.setInternalNotes("secret note not to overwrite and not to read by leseberechtigt");
        existingBooking.setOrganisationUnit(mockPerson.getOrganisationUnit());
        existingBooking.setTitle("TEST_TITLE");
        existingBooking.setSchedule(new ScheduleTemplate(
                now,
                now.plusHours(2),
                now.plusMinutes(15),
                now.plusHours(1).plusMinutes(30)));
        Booking saved = bookingRepository.save(existingBooking);

        BookingRequestDTO updateDTO = getBookingRequestDTOWithRoomAndSeating(null, null);

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

}
