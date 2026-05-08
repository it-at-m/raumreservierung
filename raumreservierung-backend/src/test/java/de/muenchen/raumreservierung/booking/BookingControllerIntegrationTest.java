package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.TestConstants.SPRING_TEST_PROFILE;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import de.muenchen.raumreservierung.TestConstants;
import de.muenchen.raumreservierung.booking.dto.BookingDetailResponseDTO;
import de.muenchen.raumreservierung.booking.dto.BookingRequestDTO;
import de.muenchen.raumreservierung.security.Authorities;
import java.util.Arrays;
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

    private HttpEntity<Object> createRequestEntity(Object body, String... authorities) {
        // Wir definieren, was passieren soll, wenn der Server das Token validiert
        org.springframework.security.oauth2.jwt.Jwt mockJwt = org.springframework.security.oauth2.jwt.Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("email", "test@muenchen.de")
                .claim("authorities", Arrays.asList(authorities))
                .subject("test-user")
                .build();

        when(jwtDecoder.decode("mock-token")).thenReturn(mockJwt);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("mock-token");
        return new HttpEntity<>(body, headers);
    }

    @Test
    void testCreateBooking_Authenticated() {
        BookingRequestDTO request = new BookingRequestDTO("Test", 100, null, false, "please clean", "no notes necessary", null, null, null, null);

        // Request mit Token und den benötigten Rollen absenden
        HttpEntity<Object> entity = createRequestEntity(request, Authorities.BOOKING_SELF);

        ResponseEntity<BookingDetailResponseDTO> response = testRestTemplate.exchange(
                BOOKINGS_URL, HttpMethod.POST, entity, BookingDetailResponseDTO.class);

        assertEquals("Status codes not equal", HttpStatus.CREATED.toString(), response.getStatusCode());
    }
}
