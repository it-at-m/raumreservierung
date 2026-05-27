package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.TestConstants.SPRING_TEST_PROFILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.raumreservierung.TestConstants;
import de.muenchen.raumreservierung.booking.dto.BookingDetailResponseDTO;
import de.muenchen.raumreservierung.booking.dto.BookingRequestDTO;
import de.muenchen.raumreservierung.person.PersonRepository;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.room.Room;
import de.muenchen.raumreservierung.room.RoomRepository;
import de.muenchen.raumreservierung.security.Roles;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
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

    private InternalPerson mockPerson;
    private Room mockRoom;
    private Room mockRoomInactive;
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

        mockBooking = new Booking();
        mockBooking.setTitle("TEST_BOOKING_TITLE");
        mockBooking.setBookedBy(mockPerson);
        mockBooking.setOrganisationUnit("TEST_UNIT");
        mockBooking = bookingRepository.save(mockBooking);

    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_ReturnsCreated_WhenAuthenticatedAndNoRRule() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingRequestDTO request = getBookingRequestDTOWithRrule(now, null);

        mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_ReturnsCreated_WhenRoomActive() throws Exception {
        BookingRequestDTO request = getBookingRequestDTOWithRoom(mockRoom.getId());

        mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_ReturnsBadRequest_WhenRoomInactive() throws Exception {
        BookingRequestDTO request = getBookingRequestDTOWithRoom(mockRoomInactive.getId());

        mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000002", authorities = { Roles.RAUM_ADMIN })
    void updateBooking_ReturnsCreated_WhenRoomActive() throws Exception {
        BookingRequestDTO request = getBookingRequestDTOWithRoom(mockRoom.getId());

        mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000002", authorities = { Roles.RAUM_ADMIN })
    void updateBooking_ReturnsBadRequest_WhenRoomInactive() throws Exception {
        BookingRequestDTO request = getBookingRequestDTOWithRoom(mockRoomInactive.getId());

        mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_ReturnsCreated_WhenAuthenticatedAndRRules(String rrule, int expectedSize, List<LocalDateTime> expectedDates) throws Exception {
        LocalDateTime date = LocalDateTime.of(2026, 3, 2, 13, 45);
        BookingRequestDTO request = getBookingRequestDTOWithRrule(date, rrule);

        String responseJson = mockMvc.perform(post(BOOKINGS_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isCreated()) // Prüft direkt auf HTTP 201
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        Assertions.assertNotNull(responseBody);

        assertThat(responseBody.appointments())
                .hasSize(expectedSize)
                .extracting(r -> r.schedule().occupancyStart())
                .containsExactlyInAnyOrderElementsOf(expectedDates);
    }

    private BookingRequestDTO getBookingRequestDTOWithRrule(LocalDateTime now, String recurringRule) {
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
                mockPerson.getId(),
                null);
    }

    private BookingRequestDTO getBookingRequestDTOWithRoom(UUID roomId) {

        ScheduleTemplate schedule = new ScheduleTemplate(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusMinutes(15),
                LocalDateTime.now().plusHours(1).plusMinutes(30));

        return new BookingRequestDTO(
                "Test",
                100,
                null,
                false,
                "please clean",
                "no notes necessary",
                null,
                roomId,
                schedule,
                mockPerson.getId(),
                null);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                Arguments.of("", 1, List.of(LocalDateTime.of(2026, 3, 2, 13, 45))),
                Arguments.of("FREQ=DAILY;COUNT=3", 3, List.of(
                        LocalDateTime.of(2026, 3, 2, 13, 45),
                        LocalDateTime.of(2026, 3, 3, 13, 45),
                        LocalDateTime.of(2026, 3, 4, 13, 45))),
                Arguments.of("FREQ=MONTHLY;COUNT=2", 2, List.of(
                        LocalDateTime.of(2026, 3, 2, 13, 45),
                        LocalDateTime.of(2026, 4, 2, 13, 45))),
                Arguments.of("FREQ=WEEKLY;COUNT=5", 5, List.of(
                        LocalDateTime.of(2026, 3, 2, 13, 45),
                        LocalDateTime.of(2026, 3, 9, 13, 45),
                        LocalDateTime.of(2026, 3, 16, 13, 45),
                        LocalDateTime.of(2026, 3, 23, 13, 45),
                        LocalDateTime.of(2026, 3, 30, 13, 45))),
                Arguments.of("FREQ=WEEKLY;BYDAY=TU,WE;INTERVAL=2;COUNT=4", 4, List.of(
                        LocalDateTime.of(2026, 3, 3, 13, 45),
                        LocalDateTime.of(2026, 3, 4, 13, 45),
                        LocalDateTime.of(2026, 3, 17, 13, 45),
                        LocalDateTime.of(2026, 3, 18, 13, 45))),
                Arguments.of("FREQ=MONTHLY;BYDAY=2WE;COUNT=3", 3, List.of(
                        LocalDateTime.of(2026, 3, 11, 13, 45),
                        LocalDateTime.of(2026, 4, 8, 13, 45),
                        LocalDateTime.of(2026, 5, 13, 13, 45))),
                Arguments.of("FREQ=MONTHLY;INTERVAL=2;BYMONTHDAY=15;COUNT=4", 4, List.of(
                        LocalDateTime.of(2026, 3, 15, 13, 45),
                        LocalDateTime.of(2026, 5, 15, 13, 45),
                        LocalDateTime.of(2026, 7, 15, 13, 45),
                        LocalDateTime.of(2026, 9, 15, 13, 45))),
                Arguments.of("FREQ=WEEKLY;INTERVAL=6", 9, List.of(
                        LocalDateTime.of(2026, 3, 2, 13, 45),
                        LocalDateTime.of(2026, 4, 13, 13, 45),
                        LocalDateTime.of(2026, 5, 25, 13, 45),
                        LocalDateTime.of(2026, 7, 6, 13, 45),
                        LocalDateTime.of(2026, 8, 17, 13, 45),
                        LocalDateTime.of(2026, 9, 28, 13, 45),
                        LocalDateTime.of(2026, 11, 9, 13, 45),
                        LocalDateTime.of(2026, 12, 21, 13, 45),
                        LocalDateTime.of(2027, 2, 1, 13, 45))));
    }

}
