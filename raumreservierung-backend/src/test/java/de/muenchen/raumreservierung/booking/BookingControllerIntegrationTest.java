package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.TestConstants.SPRING_TEST_PROFILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import de.muenchen.raumreservierung.TestConstants;
import de.muenchen.raumreservierung.booking.dto.BookingDetailResponseDTO;
import de.muenchen.raumreservierung.booking.dto.BookingRequestDTO;
import de.muenchen.raumreservierung.person.PersonService;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
public class BookingControllerIntegrationTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private static final String BOOKINGS_URL = "/bookings";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockitoBean
    private PersonService personService;

    @MockitoBean
    private org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;

    @MockitoBean
    private BookingRepository bookingRepository;

    @MockitoBean
    private EntityManager entityManager;

    private Booking savedBookingHolder;

    @BeforeEach
    void setUp() {
        InternalPerson mockPerson = new InternalPerson();
        mockPerson.setId(UUID.randomUUID());
        mockPerson.setOrganisationUnit("TEST_UNIT");

        when(personService.getInternalPersonByOrganisationIDOrThrowException(any()))
                .thenReturn(mockPerson);

        doNothing().when(entityManager).detach(any());

        when(bookingRepository.saveAndFlush(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            if (booking.getId() == null) {
                booking.setId(UUID.randomUUID());
            }
            booking.setBookedBy(mockPerson);
            savedBookingHolder = booking;
            return booking;
        });

        when(bookingRepository.findById(any(UUID.class))).thenAnswer(invocation -> Optional.ofNullable(savedBookingHolder));
    }

    @Test
    void createBooking_ReturnsCreated_WhenAuthenticatedAndNoRRule() {
        LocalDateTime now = LocalDateTime.now();
        BookingRequestDTO request = getBookingRequestDTOWithRrule(now, null);

        HttpEntity<Object> entity = createRequestEntity(request, "anwender");

        ResponseEntity<BookingDetailResponseDTO> response = testRestTemplate.exchange(
                BOOKINGS_URL, HttpMethod.POST, entity, BookingDetailResponseDTO.class);

        assertEquals("Status codes not equal", HttpStatus.CREATED, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void createBooking_ReturnsCreated_WhenAuthenticatedAndRRules(String rrule, int expectedSize, List<LocalDateTime> expectedDates) {
        LocalDateTime date = LocalDateTime.of(2026, 3, 2, 13, 45);
        BookingRequestDTO request = getBookingRequestDTOWithRrule(date, rrule);
        HttpEntity<Object> entity = createRequestEntity(request, "anwender");

        ResponseEntity<BookingDetailResponseDTO> response = testRestTemplate.exchange(
                BOOKINGS_URL, HttpMethod.POST, entity, BookingDetailResponseDTO.class);

        Assertions.assertNotNull(response.getBody());

        assertThat(response.getBody().appointments())
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
                UUID.fromString("123e4567-e89b-12d3-a456-426614174021"),
                null,
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

    private HttpEntity<Object> createRequestEntity(Object body, String... roles) {
        Map<String, Object> clientRoles = Map.of("roles", Arrays.asList(roles));
        Map<String, Object> resourceAccess = Map.of("test-client", clientRoles);

        org.springframework.security.oauth2.jwt.Jwt mockJwt = org.springframework.security.oauth2.jwt.Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("email", "test@muenchen.de")
                .claim("oid", "mock-oid-1234")
                .claim("resource_access", resourceAccess)
                .subject("test-user")
                .build();

        when(jwtDecoder.decode("mock-token")).thenReturn(mockJwt);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("mock-token");
        return new HttpEntity<>(body, headers);
    }
}
