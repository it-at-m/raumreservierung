package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.TestConstants.SPRING_TEST_PROFILE;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import de.muenchen.raumreservierung.TestConstants;
import de.muenchen.raumreservierung.booking.dto.BookingDetailResponseDTO;
import de.muenchen.raumreservierung.booking.dto.BookingRequestDTO;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
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
    private org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;

    private HttpEntity<Object> createRequestEntity(Object body, String... roles) {
        Map<String, Object> clientRoles = Map.of("roles", Arrays.asList(roles));
        Map<String, Object> resourceAccess = Map.of("test-client", clientRoles);

        org.springframework.security.oauth2.jwt.Jwt mockJwt = org.springframework.security.oauth2.jwt.Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("email", "test@muenchen.de")
                .claim("resource_access", resourceAccess) // Hier sucht dein Konverter!
                .subject("test-user")
                .build();

        when(jwtDecoder.decode("mock-token")).thenReturn(mockJwt);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("mock-token");
        return new HttpEntity<>(body, headers);
    }

    @Test
    void testCreateBooking_Authenticated() {
        LocalDateTime now = LocalDateTime.now();
        ScheduleTemplate schedule = new ScheduleTemplate(
                now,
                now.plusHours(2),
                now.plusMinutes(15),
                now.plusHours(1).plusMinutes(30));
        BookingRequestDTO request = new BookingRequestDTO(
                "Test",
                100,
                null,
                false,
                "please clean",
                "no notes necessary",
                null,
                null,
                schedule,
                null);

        // Request mit Token und den benötigten Rollen absenden
        HttpEntity<Object> entity = createRequestEntity(request, "anwender");

        ResponseEntity<BookingDetailResponseDTO> response = testRestTemplate.exchange(
                BOOKINGS_URL, HttpMethod.POST, entity, BookingDetailResponseDTO.class);

        assertEquals("Status codes not equal", HttpStatus.CREATED, response.getStatusCode());
    }
}
