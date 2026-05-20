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

    private InternalPerson mockPerson;

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
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_ReturnsCreated_WhenAuthenticatedAndNoRRule() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingRequestDTO request = getBookingRequestDTOWithRruleAndBookedFor(now, null, null);

        mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_ReturnsCreated_WhenAuthenticatedAndRRules(String rrule, int expectedSize, List<LocalDateTime> expectedDates) throws Exception {
        LocalDateTime date = LocalDateTime.of(2026, 3, 2, 13, 45);
        BookingRequestDTO request = getBookingRequestDTOWithRruleAndBookedFor(date, rrule, null);

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

    private BookingRequestDTO getBookingRequestDTOWithRruleAndBookedFor(LocalDateTime now, String recurringRule, UUID bookedForId) {
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
                bookedForId);
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

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_ShouldFallbackToCurrentPerson_WhenBookedForIsMissing() throws Exception {
        LocalDateTime now = LocalDateTime.now();
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

        LocalDateTime now = LocalDateTime.now();
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
        assertThat(responseBody.bookedFor())
                .isNotEqualTo(responseBody.bookedBy());
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

        Booking existingBooking = new Booking();
        existingBooking.setBookedBy(foreignOwner);
        existingBooking.setOrganisationUnit(foreignOwner.getOrganisationUnit());
        existingBooking.setTitle("TEST_TITLE");
        Booking saved = bookingRepository.save(existingBooking);

        BookingRequestDTO updates = getBookingRequestDTOWithRruleAndBookedFor(LocalDateTime.now(), null, null);

        mockMvc.perform(put(BOOKINGS_URL + "/" + saved.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isForbidden());
    }
}
