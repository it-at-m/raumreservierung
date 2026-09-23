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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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
    void getBookings_withFilterForRoomId_shouldReturnFilteredBookings() throws Exception {
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
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void getBookings_withFilterForTimeRange_shouldReturnBookingInPeriod() throws Exception {
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
    void getBookings_withFilterForTimeRangeAndOffset_shouldReturnBookingInPeriod() throws Exception {
        OffsetDateTime start = LocalDateTime.now().atOffset(ZoneOffset.MAX);
        OffsetDateTime end = LocalDateTime.now().atOffset(ZoneOffset.MIN);

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
        assertThat(bookings).allSatisfy(booking -> {
            assertThat(booking.schedule().occupancyStart()).isAfterOrEqualTo(start.toLocalDate().atStartOfDay(start.getOffset()).toOffsetDateTime());
            assertThat(booking.schedule().occupancyEnd()).isBeforeOrEqualTo(end.toLocalDate().atTime(LocalTime.MAX).atZone(end.getOffset()).toOffsetDateTime());
        });
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void getBookings_withFilterForStartTime_shouldReturnBookings() throws Exception {
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
        assertThat(bookings).allSatisfy(booking -> assertThat(booking.schedule().occupancyStart())
                .isAfterOrEqualTo(start.toLocalDate().atStartOfDay(start.getOffset()).toOffsetDateTime()));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void getBookings_withFilterForEndTime_shouldReturnBookings() throws Exception {
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
        assertThat(bookings).allSatisfy(booking -> assertThat(booking.schedule().occupancyEnd())
                .isBeforeOrEqualTo(end.toLocalDate().atTime(LocalTime.MAX).atZone(end.getOffset()).toOffsetDateTime()));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void getBookings_withFilterForTimeAndRoom_shouldReturnOneBooking() throws Exception {
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

    private OffsetDateTime relativeTimestamp(int dayOffset, int hourOffset, String timeStr) {
        return ZonedDateTime.of(LocalDate.now(ZoneOffset.UTC).plusDays(dayOffset), LocalTime.parse(timeStr), ZoneOffset.UTC)
                .plusHours(hourOffset)
                .toOffsetDateTime();
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void getBookings_withFilterForStartTimeWithDateChangingOffsets_shouldReturnBooking() throws Exception {
        OffsetDateTime start = relativeTimestamp(600, 1, "00:00");

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
        assertThat(bookings).allSatisfy(booking -> {
            assertThat(booking.schedule().occupancyStart()).isAfterOrEqualTo(start.toLocalDate().atStartOfDay(start.getOffset()).toOffsetDateTime());
        });
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void getBookings_withFilterForStartTimeAndEndTimeWithDateChangingOffsets_shouldReturnBooking() throws Exception {
        OffsetDateTime start = relativeTimestamp(600, 1, "00:00");
        OffsetDateTime end = relativeTimestamp(600, -1, "23:59");

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
        assertThat(bookings).allSatisfy(booking -> {
            assertThat(booking.schedule().occupancyStart()).isAfterOrEqualTo(start.toLocalDate().atStartOfDay(start.getOffset()).toOffsetDateTime());
            assertThat(booking.schedule().occupancyEnd()).isBeforeOrEqualTo(end.toLocalDate().atTime(LocalTime.MAX).atZone(end.getOffset()).toOffsetDateTime());
        });
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void getBookings_withFilterForNonExistentRoom_shouldReturnNothing() throws Exception {

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

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void getBookings_withFilterForStartStatusNEW_shouldReturnBookings() throws Exception {
        BookingStatus status = BookingStatus.NEW;

        String responseJson = mockMvc.perform(get(BOOKINGS_URL)
                .param("status", status.toString())
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
        assertThat(bookings).allSatisfy(booking -> {
            assertThat(booking.status().currentStatus()).isEqualTo(status);
        });
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void getBookings_withFilterForStartStatusNEWAndCANCELED_shouldReturnBookings() throws Exception {
        List<BookingStatus> status = List.of(BookingStatus.NEW, BookingStatus.CANCELED);

        String statusParam = status.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));

        String responseJson = mockMvc.perform(get(BOOKINGS_URL)
                .param("status", statusParam)
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
        assertThat(bookings).allSatisfy(booking -> {
            assertThat(booking.status().currentStatus()).isIn(status);
        });
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void getBookings_withFilterForTitleContainingFeier_shouldReturnBookings() throws Exception {
        final String title = "Feier";
        String responseJson = mockMvc.perform(get(BOOKINGS_URL)
                .param("title", title)
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
        assertThat(bookings).allSatisfy(booking -> {
            assertThat(booking.title()).containsIgnoringCase(title);
        });
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.RAUM_ADMIN })
    void getBookings_withFilterForBookedFor_shouldReturnBookings() throws Exception {
        final String bookedForId = "123e4567-e89b-12d3-a456-426614174016";
        String responseJson = mockMvc.perform(get(BOOKINGS_URL)
                .param("bookedForId", bookedForId)
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
        assertThat(bookings).allSatisfy(booking -> {
            assertThat(booking.bookedFor().id().toString()).isEqualTo(bookedForId);
        });
    }

}
