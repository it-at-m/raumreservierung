package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.TestConstants.SPRING_TEST_PROFILE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_STATUS_CHANGE_NOT_POSSIBLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.raumreservierung.TestConstants;
import de.muenchen.raumreservierung.appointment.Appointment;
import de.muenchen.raumreservierung.appointment.AppointmentRepository;
import de.muenchen.raumreservierung.appointment.dto.AppointmentRequestDTO;
import de.muenchen.raumreservierung.appointment.dto.AppointmentResponseDTO;
import de.muenchen.raumreservierung.booking.dto.BookingDetailResponseDTO;
import de.muenchen.raumreservierung.booking.dto.BookingRequestDTO;
import de.muenchen.raumreservierung.common.BaseEntity;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.equipment.EquipmentRepository;
import de.muenchen.raumreservierung.person.PersonRepository;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.room.Room;
import de.muenchen.raumreservierung.room.RoomRepository;
import de.muenchen.raumreservierung.room.RoomSeatingCapacity;
import de.muenchen.raumreservierung.seating.SeatingRepository;
import de.muenchen.raumreservierung.seating.SeatingType;
import de.muenchen.raumreservierung.security.Roles;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
public class BookingStatusTest {

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

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private InternalPerson mockPerson;
    private Room mockRoom2;
    private SeatingType mockSeatingType2;
    private Equipment mockEquipment2;
    private Booking mockBooking;
    private Appointment mockAppointment;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        personRepository.deleteAll();
        roomRepository.deleteAll();
        mockPerson = new InternalPerson();
        mockPerson.setOrganisationUnit("TEST_UNIT");
        mockPerson.setOrganisationId("000001");
        mockPerson.setEmail("TEST_EMAIL");
        mockPerson.setRoleFunction("anwender");
        mockPerson = personRepository.save(mockPerson);

        Room mockRoom = new Room();
        mockRoom.setName("TEST_ROOM_NAME");
        mockRoom.setNumber("100");
        mockRoom.setActive(true);
        mockRoom = roomRepository.save(mockRoom);

        mockRoom2 = new Room();
        mockRoom2.setName("TEST_ROOM_NAME_2");
        mockRoom2.setNumber("101");
        mockRoom2.setActive(true);
        mockRoom2 = roomRepository.save(mockRoom2);

        Equipment mockEquipment = new Equipment();
        mockEquipment.setName("TEST_EQUIPMENT");
        mockEquipment.setActive(true);
        mockEquipment = equipmentRepository.save(mockEquipment);

        mockEquipment2 = new Equipment();
        mockEquipment2.setName("TEST_EQUIPMENT_2");
        mockEquipment2.setActive(true);
        mockEquipment2 = equipmentRepository.save(mockEquipment2);

        SeatingType mockSeatingType = new SeatingType();
        mockSeatingType.setName("TEST_SEATING");
        mockSeatingType.setActive(true);
        mockSeatingType = seatingRepository.save(mockSeatingType);

        mockSeatingType2 = new SeatingType();
        mockSeatingType2.setName("TEST_SEATING_2");
        mockSeatingType2.setActive(true);
        mockSeatingType2 = seatingRepository.save(mockSeatingType2);

        RoomSeatingCapacity mockRoomSeatingCapacity = new RoomSeatingCapacity();
        mockRoomSeatingCapacity.setRoom(mockRoom);
        mockRoomSeatingCapacity.setSeatingType(mockSeatingType);
        mockRoomSeatingCapacity.setCapacity(5);

        RoomSeatingCapacity mockRoomSeatingCapacitySnd = new RoomSeatingCapacity();
        mockRoomSeatingCapacitySnd.setRoom(mockRoom);
        mockRoomSeatingCapacitySnd.setSeatingType(mockSeatingType2);
        mockRoomSeatingCapacitySnd.setCapacity(5);

        RoomSeatingCapacity mockRoomSeatingCapacity2 = new RoomSeatingCapacity();
        mockRoomSeatingCapacity2.setRoom(mockRoom2);
        mockRoomSeatingCapacity2.setSeatingType(mockSeatingType);
        mockRoomSeatingCapacity2.setCapacity(6);

        mockRoom.setRoomSeatingCapacities(Set.of(mockRoomSeatingCapacity, mockRoomSeatingCapacitySnd));
        mockRoom.setEquipment(Set.of(mockEquipment));
        mockRoom = roomRepository.save(mockRoom);

        mockRoom2.setRoomSeatingCapacities(Set.of(mockRoomSeatingCapacity2));
        mockRoom2.setEquipment(Set.of(mockEquipment2));
        mockRoom2 = roomRepository.save(mockRoom2);

        OffsetDateTime now = OffsetDateTime.now();
        ScheduleTemplate scheduleBooking = new ScheduleTemplate(
                now,
                now.plusHours(2),
                now.plusMinutes(15),
                now.plusHours(1).plusMinutes(30));
        ScheduleTemplate scheduleAppointment = new ScheduleTemplate(
                now,
                now.plusHours(1),
                now.plusMinutes(15),
                now.plusMinutes(30));

        mockBooking = new Booking();
        mockBooking.setTitle("TEST_BOOKING_TITLE");
        mockBooking.setBookedBy(mockPerson);
        mockBooking.setOrganisationUnit("TEST_UNIT");
        mockBooking.setRoom(mockRoom);
        mockBooking.setSeatingType(mockSeatingType);
        mockBooking.setCateringNeeded(true);
        mockBooking.setParticipantCount(1);
        mockBooking.setStatus(BookingStatus.ORGANIZER_APPROVED);
        mockBooking.setSchedule(scheduleBooking);
        mockBooking = bookingRepository.save(mockBooking);

        mockAppointment = new Appointment();
        mockAppointment.setBooking(mockBooking);
        mockAppointment.setSchedule(scheduleAppointment);
        mockAppointment = appointmentRepository.save(mockAppointment);
    }

    @ParameterizedTest
    @MethodSource("provideTestDataCreate")
    void createBooking_ReturnsCreatedAndStatusNew_WhenRoleAndStatusAny(BookingStatus status, String role) throws Exception {

        BookingRequestDTO request = getBookingRequestDTOWithStatus(status);

        String responseContent = mockMvc.perform(post(BOOKINGS_URL)
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { role })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookingDetailResponseDTO responseDto = objectMapper.readValue(responseContent, BookingDetailResponseDTO.class);

        assertThat(responseDto.status().currentStatus()).isEqualTo(BookingStatus.NEW);
    }

    private static Stream<Arguments> provideTestDataCreate() {
        return Stream.of(
                // NEW
                Arguments.of(BookingStatus.NEW, Roles.ANWENDER),
                Arguments.of(BookingStatus.NEW, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.NEW, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.NEW, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.NEW, Roles.RAUM_ADMIN),
                // ROOM_APPROVED
                Arguments.of(BookingStatus.ROOM_APPROVED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_APPROVED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ROOM_APPROVED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_APPROVED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ROOM_APPROVED, Roles.RAUM_ADMIN),
                // ROOM_CHANGED
                Arguments.of(BookingStatus.ROOM_CHANGED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_CHANGED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ROOM_CHANGED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_CHANGED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ROOM_CHANGED, Roles.RAUM_ADMIN),
                // COORDINATION_NEEDED
                Arguments.of(BookingStatus.COORDINATION_NEEDED, Roles.ANWENDER),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, Roles.RAUM_ADMIN),
                // ORGANIZER_APPROVED
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, Roles.RAUM_ADMIN),
                // ORGANIZER_CHANGED
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, Roles.RAUM_ADMIN),
                // UNFEASIBLE
                Arguments.of(BookingStatus.UNFEASIBLE, Roles.ANWENDER),
                Arguments.of(BookingStatus.UNFEASIBLE, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.UNFEASIBLE, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.UNFEASIBLE, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.UNFEASIBLE, Roles.RAUM_ADMIN),
                // CANCELED
                Arguments.of(BookingStatus.CANCELED, Roles.ANWENDER),
                Arguments.of(BookingStatus.CANCELED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.CANCELED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.CANCELED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.CANCELED, Roles.RAUM_ADMIN));
    }

    @ParameterizedTest
    @MethodSource("provideTestDataUpdate")
    void updateBooking_ReturnsOkAndAccordingStatus_WhenStatusChangeIsAllowed(BookingStatus fromStatus, BookingStatus toStatus, String roles) throws Exception {
        mockBooking.setStatus(fromStatus);
        mockBooking = bookingRepository.save(mockBooking);
        BookingRequestDTO request = getBookingRequestDTOWithStatus(toStatus);

        String responseJson = mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.status()).isNotNull();
        assertThat(responseBody.status().currentStatus()).isEqualTo(toStatus);
    }

    private static Stream<Arguments> provideTestDataUpdate() {
        return Stream.of(
                // NEW
                Arguments.of(BookingStatus.NEW, BookingStatus.NEW, Roles.ANWENDER),
                Arguments.of(BookingStatus.NEW, BookingStatus.NEW, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.NEW, BookingStatus.NEW, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.NEW, BookingStatus.NEW, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.NEW, BookingStatus.NEW, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.NEW, BookingStatus.CANCELED, Roles.ANWENDER),
                Arguments.of(BookingStatus.NEW, BookingStatus.CANCELED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.NEW, BookingStatus.CANCELED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.NEW, BookingStatus.CANCELED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.NEW, BookingStatus.CANCELED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.NEW, BookingStatus.ROOM_APPROVED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.NEW, BookingStatus.ROOM_APPROVED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.NEW, BookingStatus.UNFEASIBLE, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.NEW, BookingStatus.UNFEASIBLE, Roles.RAUM_ADMIN),
                // ROOM_APPROVED
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ROOM_APPROVED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ROOM_APPROVED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ROOM_APPROVED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ROOM_APPROVED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ROOM_APPROVED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.CANCELED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.CANCELED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.CANCELED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.CANCELED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.CANCELED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.COORDINATION_NEEDED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.COORDINATION_NEEDED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.COORDINATION_NEEDED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ORGANIZER_APPROVED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ORGANIZER_APPROVED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ORGANIZER_APPROVED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ROOM_CHANGED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ROOM_CHANGED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ROOM_CHANGED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ROOM_CHANGED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ROOM_CHANGED, Roles.RAUM_ADMIN),
                // COORDINATE_NEEDED
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.COORDINATION_NEEDED, Roles.ANWENDER),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.COORDINATION_NEEDED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.COORDINATION_NEEDED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.COORDINATION_NEEDED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.COORDINATION_NEEDED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.CANCELED, Roles.ANWENDER),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.CANCELED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.CANCELED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.CANCELED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.CANCELED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.ORGANIZER_APPROVED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.ORGANIZER_APPROVED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.ORGANIZER_APPROVED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.UNFEASIBLE, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.UNFEASIBLE, Roles.RAUM_ADMIN),
                // ORGANIZER_APPROVED
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.ORGANIZER_APPROVED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.ORGANIZER_APPROVED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.ORGANIZER_APPROVED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.ORGANIZER_APPROVED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.ORGANIZER_APPROVED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.CANCELED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.CANCELED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.CANCELED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.CANCELED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.CANCELED, Roles.RAUM_ADMIN),
                // ROOM_CHANGED
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ROOM_CHANGED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ROOM_CHANGED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ROOM_CHANGED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ROOM_CHANGED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ROOM_CHANGED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.CANCELED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.CANCELED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.CANCELED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.CANCELED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.CANCELED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ORGANIZER_CHANGED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ORGANIZER_CHANGED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.UNFEASIBLE, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.UNFEASIBLE, Roles.RAUM_ADMIN),
                // ORGANIZER_CHANGED
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ORGANIZER_CHANGED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ORGANIZER_CHANGED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ORGANIZER_CHANGED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ORGANIZER_CHANGED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ORGANIZER_CHANGED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.CANCELED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.CANCELED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.CANCELED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.CANCELED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.CANCELED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.COORDINATION_NEEDED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.COORDINATION_NEEDED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.COORDINATION_NEEDED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ORGANIZER_APPROVED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ORGANIZER_APPROVED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ORGANIZER_APPROVED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ROOM_CHANGED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ROOM_CHANGED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ROOM_CHANGED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ROOM_CHANGED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ROOM_CHANGED, Roles.RAUM_ADMIN));
    }

    @ParameterizedTest
    @MethodSource("provideTestDataBadUpdate")
    void updateBooking_ReturnsBadRequest_WhenStatusChangeIsNotAllowed(BookingStatus fromStatus, BookingStatus toStatus, String roles) throws Exception {
        mockBooking.setStatus(fromStatus);
        mockBooking = bookingRepository.save(mockBooking);
        BookingRequestDTO request = getBookingRequestDTOWithStatus(toStatus);

        mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(MSG_STATUS_CHANGE_NOT_POSSIBLE));
    }

    private static Stream<Arguments> provideTestDataBadUpdate() {
        return Stream.of(
                // NEW
                Arguments.of(BookingStatus.NEW, BookingStatus.ORGANIZER_APPROVED, Roles.ANWENDER),
                Arguments.of(BookingStatus.NEW, BookingStatus.ORGANIZER_APPROVED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.NEW, BookingStatus.ORGANIZER_APPROVED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.NEW, BookingStatus.ORGANIZER_APPROVED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.NEW, BookingStatus.ORGANIZER_APPROVED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.NEW, BookingStatus.ROOM_CHANGED, Roles.ANWENDER),
                Arguments.of(BookingStatus.NEW, BookingStatus.ROOM_CHANGED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.NEW, BookingStatus.ROOM_CHANGED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.NEW, BookingStatus.ROOM_CHANGED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.NEW, BookingStatus.ROOM_CHANGED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.NEW, BookingStatus.ORGANIZER_CHANGED, Roles.ANWENDER),
                Arguments.of(BookingStatus.NEW, BookingStatus.ORGANIZER_CHANGED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.NEW, BookingStatus.ORGANIZER_CHANGED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.NEW, BookingStatus.ORGANIZER_CHANGED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.NEW, BookingStatus.ORGANIZER_CHANGED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.NEW, BookingStatus.COORDINATION_NEEDED, Roles.ANWENDER),
                Arguments.of(BookingStatus.NEW, BookingStatus.COORDINATION_NEEDED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.NEW, BookingStatus.COORDINATION_NEEDED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.NEW, BookingStatus.COORDINATION_NEEDED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.NEW, BookingStatus.COORDINATION_NEEDED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.NEW, BookingStatus.ROOM_APPROVED, Roles.ANWENDER),
                Arguments.of(BookingStatus.NEW, BookingStatus.ROOM_APPROVED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.NEW, BookingStatus.ROOM_APPROVED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.NEW, BookingStatus.UNFEASIBLE, Roles.ANWENDER),
                Arguments.of(BookingStatus.NEW, BookingStatus.UNFEASIBLE, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.NEW, BookingStatus.UNFEASIBLE, Roles.TERMIN_ORGANISATOR),
                // ROOM_APPROVED
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.NEW, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.NEW, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.NEW, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.NEW, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.NEW, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.UNFEASIBLE, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.UNFEASIBLE, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.UNFEASIBLE, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.UNFEASIBLE, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.UNFEASIBLE, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ORGANIZER_CHANGED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ORGANIZER_CHANGED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ORGANIZER_CHANGED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ORGANIZER_CHANGED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ORGANIZER_CHANGED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ORGANIZER_APPROVED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.ORGANIZER_APPROVED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.COORDINATION_NEEDED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_APPROVED, BookingStatus.COORDINATION_NEEDED, Roles.LESEBERECHTIGT),
                // COORDINATE_NEEDED
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.NEW, Roles.ANWENDER),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.NEW, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.NEW, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.NEW, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.NEW, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.ROOM_APPROVED, Roles.ANWENDER),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.ROOM_APPROVED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.ROOM_APPROVED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.ROOM_APPROVED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.ROOM_APPROVED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.ORGANIZER_CHANGED, Roles.ANWENDER),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.ORGANIZER_CHANGED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.ORGANIZER_CHANGED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.ORGANIZER_CHANGED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.ORGANIZER_CHANGED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.UNFEASIBLE, Roles.ANWENDER),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.UNFEASIBLE, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.UNFEASIBLE, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.ORGANIZER_APPROVED, Roles.ANWENDER),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, BookingStatus.ORGANIZER_APPROVED, Roles.LESEBERECHTIGT),
                // ORGANIZER_APPROVED
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.NEW, Roles.ANWENDER),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.NEW, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.NEW, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.NEW, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.NEW, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.UNFEASIBLE, Roles.ANWENDER),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.UNFEASIBLE, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.UNFEASIBLE, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.UNFEASIBLE, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.UNFEASIBLE, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.COORDINATION_NEEDED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.COORDINATION_NEEDED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.COORDINATION_NEEDED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.COORDINATION_NEEDED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.COORDINATION_NEEDED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.ROOM_APPROVED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.ROOM_APPROVED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.ROOM_APPROVED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.ROOM_APPROVED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.ROOM_APPROVED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.ORGANIZER_CHANGED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.ORGANIZER_CHANGED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.ORGANIZER_CHANGED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.ORGANIZER_CHANGED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, BookingStatus.ORGANIZER_CHANGED, Roles.RAUM_ADMIN),
                // ROOM_CHANGED
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.NEW, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.NEW, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.NEW, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.NEW, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.NEW, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ROOM_APPROVED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ROOM_APPROVED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ROOM_APPROVED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ROOM_APPROVED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ROOM_APPROVED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.COORDINATION_NEEDED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.COORDINATION_NEEDED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.COORDINATION_NEEDED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.COORDINATION_NEEDED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.COORDINATION_NEEDED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ORGANIZER_APPROVED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ORGANIZER_APPROVED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ORGANIZER_APPROVED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ORGANIZER_APPROVED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ORGANIZER_APPROVED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ORGANIZER_CHANGED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ORGANIZER_CHANGED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.ORGANIZER_CHANGED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.UNFEASIBLE, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.UNFEASIBLE, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ROOM_CHANGED, BookingStatus.UNFEASIBLE, Roles.TERMIN_ORGANISATOR),
                // ORGANIZER_CHANGED
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.NEW, Roles.ANWENDER),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.NEW, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.NEW, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.NEW, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.NEW, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.UNFEASIBLE, Roles.ANWENDER),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.UNFEASIBLE, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.UNFEASIBLE, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.UNFEASIBLE, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.UNFEASIBLE, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ROOM_APPROVED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ROOM_APPROVED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ROOM_APPROVED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ROOM_APPROVED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ROOM_APPROVED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.COORDINATION_NEEDED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.COORDINATION_NEEDED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ORGANIZER_APPROVED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ORGANIZER_APPROVED, Roles.LESEBERECHTIGT));
    }

    // tests for automatic change of status (depending on role) by change of room
    @ParameterizedTest
    @MethodSource("provideTestDataUpdateChanged")
    void updateBooking_ReturnsOkAndROOM_CHANGEDStatus_WhenRoomChangedAndAnwenderOrLeseberechtigt(BookingStatus fromStatus, String roles) throws Exception {
        mockBooking.setStatus(fromStatus);
        bookingRepository.save(mockBooking);
        mockBooking.setRoom(mockRoom2);
        BookingRequestDTO request = getBookingRequestDTOFromBooking(mockBooking);

        String responseJson = mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.status()).isNotNull();
        assertThat(responseBody.status().currentStatus()).isEqualTo(BookingStatus.ROOM_CHANGED);
    }

    private static Stream<Arguments> provideTestDataUpdateChanged() {
        return Stream.of(
                Arguments.of(BookingStatus.ROOM_APPROVED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ROOM_APPROVED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, Roles.ANWENDER),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, Roles.LESEBERECHTIGT),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, Roles.ANWENDER),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, Roles.LESEBERECHTIGT));
    }

    // tests for no change of status (depending on role) by change of room
    @ParameterizedTest
    @MethodSource("provideTestDataUpdateChangedAdmin")
    void updateBooking_ReturnsOkAndOldStatus_WhenRoomChangedAndTerminorganisatorOrRaumbuchungOrRaumadmin(BookingStatus fromStatus, String roles)
            throws Exception {
        mockBooking.setStatus(fromStatus);
        bookingRepository.save(mockBooking);
        mockBooking.setRoom(mockRoom2);
        BookingRequestDTO request = getBookingRequestDTOFromBooking(mockBooking);

        String responseJson = mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.status()).isNotNull();
        assertThat(responseBody.status().currentStatus()).isEqualTo(fromStatus);
    }

    private static Stream<Arguments> provideTestDataUpdateChangedAdmin() {
        return Stream.of(
                Arguments.of(BookingStatus.ROOM_APPROVED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ROOM_APPROVED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ROOM_APPROVED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.COORDINATION_NEEDED, Roles.RAUM_ADMIN),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, Roles.TERMIN_ORGANISATOR),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, Roles.RAUM_BUCHUNG),
                Arguments.of(BookingStatus.ORGANIZER_APPROVED, Roles.RAUM_ADMIN));
    }

    // tests for automatic change of status (depending on role) by change of equipment
    @ParameterizedTest
    @MethodSource("provideTestDataUpdateChanged")
    void updateBooking_ReturnsOkAndROOM_CHANGEDStatus_WhenEquipmentChangedAndAnwenderOrLeseberechtigt(BookingStatus fromStatus, String roles) throws Exception {
        mockBooking.setStatus(fromStatus);
        bookingRepository.save(mockBooking);
        mockBooking.setEquipment(Set.of(mockEquipment2));
        BookingRequestDTO request = getBookingRequestDTOFromBooking(mockBooking);

        String responseJson = mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.status()).isNotNull();
        assertThat(responseBody.status().currentStatus()).isEqualTo(BookingStatus.ROOM_CHANGED);
    }

    // tests for no change of status (depending on role) by change of equipment
    @ParameterizedTest
    @MethodSource("provideTestDataUpdateChangedAdmin")
    void updateBooking_ReturnsOkAndOldStatus_WhenEquipmentChangedAndTerminorganisatorOrRaumbuchungOrRaumadmin(BookingStatus fromStatus, String roles)
            throws Exception {
        mockBooking.setStatus(fromStatus);
        bookingRepository.save(mockBooking);
        mockBooking.setEquipment(Set.of(mockEquipment2));
        BookingRequestDTO request = getBookingRequestDTOFromBooking(mockBooking);

        String responseJson = mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.status()).isNotNull();
        assertThat(responseBody.status().currentStatus()).isEqualTo(fromStatus);
    }

    // tests for automatic change of status (depending on role) by change of seating type
    @ParameterizedTest
    @MethodSource("provideTestDataUpdateChanged")
    void updateBooking_ReturnsOkAndROOM_CHANGEDStatus_WhenSeatingTypeChangedAndAnwenderOrLeseberechtigt(BookingStatus fromStatus, String roles)
            throws Exception {
        mockBooking.setStatus(fromStatus);
        bookingRepository.save(mockBooking);
        mockBooking.setSeatingType(mockSeatingType2);
        BookingRequestDTO request = getBookingRequestDTOFromBooking(mockBooking);

        String responseJson = mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.status()).isNotNull();
        assertThat(responseBody.status().currentStatus()).isEqualTo(BookingStatus.ROOM_CHANGED);
    }

    // tests for no change of status (depending on role) by change of seating type
    @ParameterizedTest
    @MethodSource("provideTestDataUpdateChangedAdmin")
    void updateBooking_ReturnsOkAndOldStatus_WhenSeatingTypeChangedAndTerminorganisatorOrRaumbuchungOrRaumadmin(BookingStatus fromStatus, String roles)
            throws Exception {
        mockBooking.setStatus(fromStatus);
        bookingRepository.save(mockBooking);
        mockBooking.setSeatingType(mockSeatingType2);
        BookingRequestDTO request = getBookingRequestDTOFromBooking(mockBooking);

        String responseJson = mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.status()).isNotNull();
        assertThat(responseBody.status().currentStatus()).isEqualTo(fromStatus);
    }

    // tests for automatic change of status (depending on role) by change of participant count
    @ParameterizedTest
    @MethodSource("provideTestDataUpdateChanged")
    void updateBooking_ReturnsOkAndROOM_CHANGEDStatus_WhenParticipantCountChangedAndAnwenderOrLeseberechtigt(BookingStatus fromStatus, String roles)
            throws Exception {
        mockBooking.setStatus(fromStatus);
        bookingRepository.save(mockBooking);
        mockBooking.setParticipantCount(2);
        BookingRequestDTO request = getBookingRequestDTOFromBooking(mockBooking);

        String responseJson = mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.status()).isNotNull();
        assertThat(responseBody.status().currentStatus()).isEqualTo(BookingStatus.ROOM_CHANGED);
    }

    // tests for no change of status (depending on role) by change of participant count
    @ParameterizedTest
    @MethodSource("provideTestDataUpdateChangedAdmin")
    void updateBooking_ReturnsOkAndOldStatus_WhenParticipantCountChangedAndTerminorganisatorOrRaumbuchungOrRaumadmin(BookingStatus fromStatus,
            String roles) throws Exception {
        mockBooking.setStatus(fromStatus);
        bookingRepository.save(mockBooking);
        mockBooking.setParticipantCount(2);
        BookingRequestDTO request = getBookingRequestDTOFromBooking(mockBooking);

        String responseJson = mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.status()).isNotNull();
        assertThat(responseBody.status().currentStatus()).isEqualTo(fromStatus);
    }

    // tests for automatic change of status (depending on role) by change of catering needed
    @ParameterizedTest
    @MethodSource("provideTestDataUpdateChanged")
    void updateBooking_ReturnsOkAndROOM_CHANGEDStatus_WhenCateringNeededChangedAndAnwenderOrLeseberechtigt(BookingStatus fromStatus, String roles)
            throws Exception {
        mockBooking.setStatus(fromStatus);
        bookingRepository.save(mockBooking);
        mockBooking.setCateringNeeded(false);
        BookingRequestDTO request = getBookingRequestDTOFromBooking(mockBooking);

        String responseJson = mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.status()).isNotNull();
        assertThat(responseBody.status().currentStatus()).isEqualTo(BookingStatus.ROOM_CHANGED);
    }

    // tests for no change of status (depending on role) by change of catering needed
    @ParameterizedTest
    @MethodSource("provideTestDataUpdateChangedAdmin")
    void updateBooking_ReturnsOkAndOldStatus_WhenCateringNeededChangedAndTerminorganisatorOrRaumbuchungOrRaumadmin(BookingStatus fromStatus,
            String roles) throws Exception {
        mockBooking.setStatus(fromStatus);
        bookingRepository.save(mockBooking);
        mockBooking.setCateringNeeded(false);
        BookingRequestDTO request = getBookingRequestDTOFromBooking(mockBooking);

        String responseJson = mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.status()).isNotNull();
        assertThat(responseBody.status().currentStatus()).isEqualTo(fromStatus);
    }

    // tests for automatic change of status (depending on role) by change of schedule
    @ParameterizedTest
    @MethodSource("provideTestDataUpdateChanged")
    void updateBooking_ReturnsOkAndROOM_CHANGEDStatus_WhenScheduleChangedAndAnwenderOrLeseberechtigt(BookingStatus fromStatus, String roles) throws Exception {
        mockBooking.setStatus(fromStatus);
        bookingRepository.save(mockBooking);
        BookingRequestDTO request = getBookingRequestDTOFromDate(OffsetDateTime.now().plusHours(1));

        String responseJson = mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.status()).isNotNull();
        assertThat(responseBody.status().currentStatus()).isEqualTo(BookingStatus.ROOM_CHANGED);
    }

    // tests for no change of status (depending on role) by change of schedule
    @ParameterizedTest
    @MethodSource("provideTestDataUpdateChangedAdmin")
    void updateBooking_ReturnsOkAndOldStatus_WhenScheduleChangedAndTerminorganisatorOrRaumbuchungOrRaumadmin(BookingStatus fromStatus, String roles)
            throws Exception {
        mockBooking.setStatus(fromStatus);
        bookingRepository.save(mockBooking);
        BookingRequestDTO request = getBookingRequestDTOFromDate(OffsetDateTime.now().plusHours(1));

        String responseJson = mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.status()).isNotNull();
        assertThat(responseBody.status().currentStatus()).isEqualTo(fromStatus);
    }

    // tests for automatic change of status (depending on role) by change of recurring rule
    @ParameterizedTest
    @MethodSource("provideTestDataUpdateChanged")
    void updateBooking_ReturnsOkAndROOM_CHANGEDStatus_WhenRRuleChangedAndAnwenderOrLeseberechtigt(BookingStatus fromStatus, String roles) throws Exception {
        mockBooking.setStatus(fromStatus);
        bookingRepository.save(mockBooking);
        mockBooking.setRecurringRule("RRULE:FREQ=WEEKLY;COUNT=5");
        BookingRequestDTO request = getBookingRequestDTOFromBooking(mockBooking);

        String responseJson = mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.status()).isNotNull();
        assertThat(responseBody.status().currentStatus()).isEqualTo(BookingStatus.ROOM_CHANGED);
    }

    // tests for no change of status (depending on role) by change of recurring rule
    @ParameterizedTest
    @MethodSource("provideTestDataUpdateChangedAdmin")
    void updateBooking_ReturnsOkAndOldStatus_WhenRRuleChangedAndTerminorganisatorOrRaumbuchungOrRaumadmin(BookingStatus fromStatus, String roles)
            throws Exception {
        mockBooking.setStatus(fromStatus);
        bookingRepository.save(mockBooking);
        mockBooking.setRecurringRule("RRULE:FREQ=WEEKLY;COUNT=5");
        BookingRequestDTO request = getBookingRequestDTOFromBooking(mockBooking);

        String responseJson = mockMvc.perform(put(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        BookingDetailResponseDTO responseBody = objectMapper.readValue(responseJson, BookingDetailResponseDTO.class);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.status()).isNotNull();
        assertThat(responseBody.status().currentStatus()).isEqualTo(fromStatus);
    }

    // tests for automatic change of status (depending on role) by change of schedule of appointment
    @ParameterizedTest
    @MethodSource("provideTestDataUpdateChanged")
    void updateAppointment_ReturnsOkAndROOM_CHANGEDStatus_WhenScheduleChangedAndAnwenderOrLeseberechtigt(BookingStatus fromStatus, String roles)
            throws Exception {
        mockBooking.setStatus(fromStatus);
        mockBooking = bookingRepository.save(mockBooking);

        AppointmentRequestDTO request = getAppointmentRequestDTOFromDate(OffsetDateTime.now().plusMinutes(1));

        String responseJson = mockMvc.perform(put(APPOINTMENTS_URL + "/" + mockAppointment.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        AppointmentResponseDTO responseBody = objectMapper.readValue(responseJson, AppointmentResponseDTO.class);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.schedule()).isNotNull();
        assertThat(responseBody.schedule()).usingRecursiveComparison()
                .withEqualsForType(OffsetDateTime::isEqual, OffsetDateTime.class)
                .isEqualTo(request.schedule());

        String bookingResponseJson = mockMvc.perform(get(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        BookingDetailResponseDTO bookingResponseBody = objectMapper.readValue(bookingResponseJson, BookingDetailResponseDTO.class);
        assertThat(bookingResponseBody).isNotNull();
        assertThat(bookingResponseBody.status()).isNotNull();
        assertThat(bookingResponseBody.status().currentStatus()).isEqualTo(BookingStatus.ROOM_CHANGED);

    }

    // tests for no change of status (depending on role) by change of schedule of appointment
    @ParameterizedTest
    @MethodSource("provideTestDataUpdateChangedAdmin")
    void updateAppointment_ReturnsOkAndOldStatus_WhenScheduleChangedAndTerminorganisatorOrRaumbuchungOrRaumadmin(BookingStatus fromStatus, String roles)
            throws Exception {
        mockBooking.setStatus(fromStatus);
        mockBooking = bookingRepository.save(mockBooking);

        AppointmentRequestDTO request = getAppointmentRequestDTOFromDate(OffsetDateTime.now().plusMinutes(1));

        String responseJson = mockMvc.perform(put(APPOINTMENTS_URL + "/" + mockAppointment.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        AppointmentResponseDTO responseBody = objectMapper.readValue(responseJson, AppointmentResponseDTO.class);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.schedule()).isNotNull();
        assertThat(responseBody.schedule()).usingRecursiveComparison()
                .withEqualsForType(OffsetDateTime::isEqual, OffsetDateTime.class)
                .isEqualTo(request.schedule());

        String bookingResponseJson = mockMvc.perform(get(BOOKINGS_URL + "/" + mockBooking.getId())
                .with(csrf())
                .with(jwt()
                        .jwt(jwt -> jwt.claim("lhmObjectID", "000001"))
                        .authorities(Arrays.stream(new String[] { roles })
                                .map(SimpleGrantedAuthority::new)
                                .toArray(GrantedAuthority[]::new)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        BookingDetailResponseDTO bookingResponseBody = objectMapper.readValue(bookingResponseJson, BookingDetailResponseDTO.class);
        assertThat(bookingResponseBody).isNotNull();
        assertThat(bookingResponseBody.status()).isNotNull();
        assertThat(bookingResponseBody.status().currentStatus()).isEqualTo(fromStatus);
    }

    private BookingRequestDTO getBookingRequestDTOWithStatus(BookingStatus status) {
        OffsetDateTime now = OffsetDateTime.now();
        mockBooking.setStatus(status);
        return getBookingRequestDTOFromBookingAndDate(mockBooking, now);
    }

    private BookingRequestDTO getBookingRequestDTOFromBooking(Booking booking) {
        return getBookingRequestDTOFromBookingAndDate(booking, OffsetDateTime.now());
    }

    private BookingRequestDTO getBookingRequestDTOFromDate(OffsetDateTime now) {
        return getBookingRequestDTOFromBookingAndDate(mockBooking, now);
    }

    private AppointmentRequestDTO getAppointmentRequestDTOFromDate(OffsetDateTime now) {
        return new AppointmentRequestDTO(
                new ScheduleTemplate(
                        now,
                        now.plusHours(1),
                        now.plusMinutes(15),
                        now.plusMinutes(30)));
    }

    private BookingRequestDTO getBookingRequestDTOFromBookingAndDate(Booking booking, OffsetDateTime now) {
        ScheduleTemplate schedule = new ScheduleTemplate(
                now,
                now.plusHours(2),
                now.plusMinutes(15),
                now.plusHours(1).plusMinutes(30));

        return new BookingRequestDTO(
                "Test",
                booking.getParticipantCount(),
                booking.getEquipment().stream().map(BaseEntity::getId).toList(),
                booking.isCateringNeeded(),
                "secret note",
                "no notes necessary",
                booking.getRecurringRule(),
                booking.getRoom().getId(),
                schedule,
                mockPerson.getId(),
                booking.getSeatingType().getId(),
                booking.getStatus(),
                booking.getReasonForRejection());
    }

}
