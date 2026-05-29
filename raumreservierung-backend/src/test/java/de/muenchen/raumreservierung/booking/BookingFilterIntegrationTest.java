package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.TestConstants.SPRING_TEST_PROFILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import de.muenchen.raumreservierung.TestConstants;
import de.muenchen.raumreservierung.booking.dto.BookingListResponseDTO;
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
public class BookingFilterIntegrationTest {

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

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void getBookings_withRoomIdFilter_shouldReturnFilteredBookings() throws Exception {
        UUID roomId = UUID.fromString("770e8400-e29b-41d4-a716-446655440001");

        String responseJson = mockMvc.perform(get(BOOKINGS_URL)
                .param("roomId", roomId.toString())
                .param("page", "0")
                .param("size", "10")
                .param("self", "false"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        DocumentContext jsonContext = JsonPath.parse(responseJson);
        List<String> roomIds = jsonContext.read("$.content[*].room.id");

        assertThat(roomIds).isNotEmpty();
        assertThat(roomIds).containsOnly(roomId.toString());
        assertThat(roomIds.size()).isEqualTo(2);
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void getBookings_withTimeRangeFilterNow_shouldReturnOneBookingInPeriod() throws Exception {
        OffsetDateTime start = OffsetDateTime.now();
        OffsetDateTime end = OffsetDateTime.now();

        String responseJson = mockMvc.perform(get(BOOKINGS_URL)
                .param("start", start.toString())
                .param("end", end.toString())
                .param("page", "0")
                .param("size", "10")
                .param("self", "false"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String contentJson = JsonPath.read(responseJson, "$.content").toString();
        List<BookingListResponseDTO> bookings = objectMapper.readValue(
                contentJson,
                new TypeReference<>() {
                });

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings).allSatisfy(booking -> {
            assertThat(booking.schedule().occupancyStart()).isAfterOrEqualTo(start.toLocalDate().atStartOfDay(start.getOffset()).toOffsetDateTime());
            assertThat(booking.schedule().occupancyEnd()).isBeforeOrEqualTo(end.toLocalDate().atTime(LocalTime.MAX).atZone(end.getOffset()).toOffsetDateTime());
        });
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void getBookings_withStartTimeFilterNow_shouldReturnFourBookings() throws Exception {
        OffsetDateTime start = OffsetDateTime.now();

        String responseJson = mockMvc.perform(get(BOOKINGS_URL)
                .param("start", start.toString())
                .param("page", "0")
                .param("size", "10")
                .param("self", "false"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String contentJson = JsonPath.read(responseJson, "$.content").toString();
        List<BookingListResponseDTO> bookings = objectMapper.readValue(
                contentJson,
                new TypeReference<>() {
                });

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(4);
        assertThat(bookings).allSatisfy(booking -> assertThat(booking.schedule().occupancyStart())
                .isAfterOrEqualTo(start.toLocalDate().atStartOfDay(start.getOffset()).toOffsetDateTime()));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void getBookings_withEndTimeFilterNow_shouldReturnSixBookings() throws Exception {
        OffsetDateTime end = OffsetDateTime.now();

        String responseJson = mockMvc.perform(get(BOOKINGS_URL)
                .param("end", end.toString())
                .param("page", "0")
                .param("size", "10")
                .param("self", "false"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String contentJson = JsonPath.read(responseJson, "$.content").toString();
        List<BookingListResponseDTO> bookings = objectMapper.readValue(
                contentJson,
                new TypeReference<>() {
                });

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(6);
        assertThat(bookings).allSatisfy(booking -> assertThat(booking.schedule().occupancyEnd())
                .isBeforeOrEqualTo(end.toLocalDate().atTime(LocalTime.MAX).atZone(end.getOffset()).toOffsetDateTime()));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void getBookings_withFullFilter_shouldReturnOneBooking() throws Exception {
        OffsetDateTime end = OffsetDateTime.now();
        OffsetDateTime start = OffsetDateTime.now();
        UUID roomId = UUID.fromString("770e8400-e29b-41d4-a716-446655440001");

        String responseJson = mockMvc.perform(get(BOOKINGS_URL)
                .param("end", end.toString())
                .param("start", start.toString())
                .param("roomId", roomId.toString())
                .param("page", "0")
                .param("size", "10")
                .param("self", "false"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String contentJson = JsonPath.read(responseJson, "$.content").toString();
        List<BookingListResponseDTO> bookings = objectMapper.readValue(
                contentJson,
                new TypeReference<>() {
                });

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings).allSatisfy(booking -> {
            assertThat(booking.schedule().occupancyStart()).isAfterOrEqualTo(start.toLocalDate().atStartOfDay(start.getOffset()).toOffsetDateTime());
            assertThat(booking.schedule().occupancyEnd()).isBeforeOrEqualTo(end.toLocalDate().atTime(LocalTime.MAX).atZone(end.getOffset()).toOffsetDateTime());
            assertThat(booking.room().id()).isEqualTo(roomId);
        });
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void getBookings_withNonExistantRoom_shouldReturnNothing() throws Exception {

        UUID roomId = UUID.fromString("770e8400-e29b-41d4-a716-446655440005");

        String responseJson = mockMvc.perform(get(BOOKINGS_URL)
                .param("roomId", roomId.toString())
                .param("page", "0")
                .param("size", "10")
                .param("self", "false"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String contentJson = JsonPath.read(responseJson, "$.content").toString();
        List<BookingListResponseDTO> bookings = objectMapper.readValue(
                contentJson,
                new TypeReference<>() {
                });

        assertThat(bookings).isEmpty();
    }

}
