package de.muenchen.raumreservierung.appointment;

import static de.muenchen.raumreservierung.TestConstants.SPRING_TEST_PROFILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import de.muenchen.raumreservierung.TestConstants;
import de.muenchen.raumreservierung.appointment.dto.AppointmentDetailsResponseDTO;
import de.muenchen.raumreservierung.booking.WithMockJwt;
import de.muenchen.raumreservierung.security.Roles;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
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
@TestPropertySource(
        properties = {
                "spring.jpa.hibernate.ddl-auto=validate"
        }
)
public class AppointmentFilterIntegrationTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private static final String APPOINTMENTS_URL = "/appointments";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.LESEBERECHTIGT })
    void getAppointmentsByTimePeriod_ShouldReturnAppointments() throws Exception {
        OffsetDateTime start = OffsetDateTime.now();
        OffsetDateTime end = OffsetDateTime.now().plusDays(10);

        String responseJson = mockMvc.perform(get(APPOINTMENTS_URL)
                .param("startDate", start.toString())
                .param("endDate", end.toString())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String contentJson = JsonPath.read(responseJson, "$.content").toString();
        List<AppointmentDetailsResponseDTO> appointments = objectMapper.readValue(
                contentJson,
                new TypeReference<>() {
                });

        assertThat(appointments).isNotEmpty();
        assertThat(appointments).allSatisfy(appointment -> {
            assertThat(appointment.schedule().occupancyStart()).isAfterOrEqualTo(start.toLocalDate().atStartOfDay(start.getOffset()).toOffsetDateTime());
            assertThat(appointment.schedule().occupancyEnd())
                    .isBeforeOrEqualTo(end.toLocalDate().atTime(LocalTime.MAX).atZone(end.getOffset()).toOffsetDateTime());
        });
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.LESEBERECHTIGT })
    void getAppointmentsByPageableAndFilter_WithInvalidFilter_ShouldReturnBadRequest() throws Exception {
        UUID roomId = UUID.fromString("770e8400-e29b-41d4-a716-446655440001");

        mockMvc.perform(get(APPOINTMENTS_URL)
                .param("roomIds", roomId.toString())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.LESEBERECHTIGT })
    void getAppointmentsByTimePeriodAndRoom_ShouldReturnAppointments() throws Exception {
        OffsetDateTime start = OffsetDateTime.now();
        OffsetDateTime end = OffsetDateTime.now().plusDays(10);
        UUID roomId = UUID.fromString("770e8400-e29b-41d4-a716-446655440004");

        String responseJson = mockMvc.perform(get(APPOINTMENTS_URL)
                .param("roomIds", roomId.toString())
                .param("startDate", start.toString())
                .param("endDate", end.toString())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String contentJson = JsonPath.read(responseJson, "$.content").toString();
        List<AppointmentDetailsResponseDTO> appointments = objectMapper.readValue(
                contentJson,
                new TypeReference<>() {
                });

        assertThat(appointments).isNotEmpty();
        assertThat(appointments).allSatisfy(appointment -> {
            assertThat(appointment.schedule().occupancyStart()).isAfterOrEqualTo(start.toLocalDate().atStartOfDay(start.getOffset()).toOffsetDateTime());
            assertThat(appointment.schedule().occupancyEnd())
                    .isBeforeOrEqualTo(end.toLocalDate().atTime(LocalTime.MAX).atZone(end.getOffset()).toOffsetDateTime());
            assertThat(appointment.bookingMinimal().roomId()).isEqualTo(roomId);
        });
    }
}
