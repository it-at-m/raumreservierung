package de.muenchen.raumreservierung.seating;

import static de.muenchen.raumreservierung.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.muenchen.raumreservierung.TestConstants;
import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.booking.BookingRepository;
import de.muenchen.raumreservierung.booking.BookingStatus;
import de.muenchen.raumreservierung.booking.BookingType;
import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import de.muenchen.raumreservierung.booking.WithMockJwt;
import de.muenchen.raumreservierung.person.PersonRepository;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.room.Room;
import de.muenchen.raumreservierung.room.RoomRepository;
import de.muenchen.raumreservierung.security.Roles;
import java.time.OffsetDateTime;
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
import org.springframework.security.oauth2.jwt.JwtDecoder;
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
public class SeatingControllerIntegrationTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private static final String SEATING_URL = "/seating";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private SeatingRepository seatingRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PersonRepository personRepository;

    private InternalPerson mockPerson;
    private Room mockRoom;
    private SeatingType mockSeatingType;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
        personRepository.deleteAll();
        seatingRepository.deleteAll();

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
        mockRoom.setCapacity(100);
        mockRoom = roomRepository.save(mockRoom);

        mockSeatingType = new SeatingType();
        mockSeatingType.setName("TEST_SEATING");
        mockSeatingType.setActive(true);
        mockSeatingType = seatingRepository.save(mockSeatingType);
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void isSeatingTypeDeletable_ReturnsTrue_WhenNoBookingReferencesSeatingType() throws Exception {
        mockMvc.perform(get(SEATING_URL + "/" + mockSeatingType.getId() + "/deletable")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void isSeatingTypeDeletable_ReturnsTrue_WhenOnlyPastBookingReferencesSeatingType() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();
        saveBookingWithSeatingType(mockSeatingType, BookingStatus.ORGANIZER_APPROVED, now.minusDays(1));

        mockMvc.perform(get(SEATING_URL + "/" + mockSeatingType.getId() + "/deletable")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @ParameterizedTest
    @MethodSource("provideStatusAndExpectedDeletability")
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void isSeatingTypeDeletableOfFutureBooking_DependsOnBookingStatus(BookingStatus status, boolean expectedDeletable) throws Exception {
        OffsetDateTime now = OffsetDateTime.now();
        saveBookingWithSeatingType(mockSeatingType, status, now.plusDays(1));

        mockMvc.perform(get(SEATING_URL + "/" + mockSeatingType.getId() + "/deletable")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedDeletable)));
    }

    private static Stream<Arguments> provideStatusAndExpectedDeletability() {
        return Stream.of(
                Arguments.of(BookingStatus.CANCELED, true),
                Arguments.of(BookingStatus.UNFEASIBLE, true),
                Arguments.of(BookingStatus.NEW, true),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, false));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void isSeatingTypeDeletable_ReturnsFalse_WhenOnlyOneOfMultipleBookingsIsRelevant() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();
        saveBookingWithSeatingType(mockSeatingType, BookingStatus.CANCELED, now.plusDays(1));
        saveBookingWithSeatingType(mockSeatingType, BookingStatus.ORGANIZER_APPROVED, now.minusDays(1));
        saveBookingWithSeatingType(mockSeatingType, BookingStatus.ORGANIZER_APPROVED, now.plusDays(2));

        mockMvc.perform(get(SEATING_URL + "/" + mockSeatingType.getId() + "/deletable")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    private void saveBookingWithSeatingType(SeatingType seatingType, BookingStatus status, OffsetDateTime occupancyEnd) {
        Booking booking = new Booking();
        booking.setTitle("TEST_BOOKING_TITLE");
        booking.setBookedBy(mockPerson);
        booking.setBookedFor(mockPerson);
        booking.setOrganisationUnit(mockPerson.getOrganisationUnit());
        booking.setRoom(mockRoom);
        booking.setSeatingType(seatingType);
        booking.setStatus(status);
        booking.setSchedule(new ScheduleTemplate(
                occupancyEnd.minusHours(2),
                occupancyEnd,
                occupancyEnd.minusHours(1).minusMinutes(45),
                occupancyEnd.minusMinutes(30)));
        booking.setBookingType(BookingType.DEFAULT);
        bookingRepository.save(booking);
    }
}
