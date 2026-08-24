package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.TestConstants.SPRING_TEST_PROFILE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.muenchen.raumreservierung.TestConstants;
import de.muenchen.raumreservierung.appointment.Appointment;
import de.muenchen.raumreservierung.appointment.AppointmentRepository;
import de.muenchen.raumreservierung.appointment.AppointmentService;
import de.muenchen.raumreservierung.common.BadRequestException;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.equipment.EquipmentRepository;
import de.muenchen.raumreservierung.person.ExternalPersonRepository;
import de.muenchen.raumreservierung.person.InternalPersonRepository;
import de.muenchen.raumreservierung.person.domain.ExternalPerson;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.room.Room;
import de.muenchen.raumreservierung.room.RoomRepository;
import de.muenchen.raumreservierung.room.RoomSeatingCapacity;
import de.muenchen.raumreservierung.seating.SeatingRepository;
import de.muenchen.raumreservierung.seating.SeatingType;
import de.muenchen.raumreservierung.security.Roles;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingServiceIntegrationTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    @Autowired
    private BookingService bookingService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private InternalPersonRepository internalPersonRepository;
    @Autowired
    private ExternalPersonRepository externalPersonRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private SeatingRepository seatingRepository;
    @Autowired
    private PlatformTransactionManager txManager;
    @MockitoBean
    private org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;

    private Booking baseBooking;
    private Booking existingBooking;
    private ExternalPerson externalPerson;
    private InternalPerson internalPerson;
    private InternalPerson internalPersonAdmin;
    private Room room2;
    private SeatingType seatingType2;
    private Equipment equipment;
    private Appointment appointmentUpdate;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        internalPersonRepository.deleteAll();
        externalPersonRepository.deleteAll();
        roomRepository.deleteAll();
        equipmentRepository.deleteAll();
        seatingRepository.deleteAll();
        appointmentRepository.deleteAll();

        internalPerson = new InternalPerson();
        internalPerson.setOrganisationId("000001");
        internalPerson.setOrganisationUnit("ITM");
        internalPerson.setEmail("internal@person.de");
        internalPerson = internalPersonRepository.save(internalPerson);

        internalPersonAdmin = new InternalPerson();
        internalPersonAdmin.setOrganisationId("000002");
        internalPersonAdmin.setOrganisationUnit("ITM");
        internalPersonAdmin.setEmail("internalAdmin@person.de");
        internalPersonAdmin = internalPersonRepository.save(internalPersonAdmin);

        externalPerson = new ExternalPerson();
        externalPerson.setEmail("external@person.de");
        externalPerson.setLastModified(LocalDate.now());
        externalPerson = externalPersonRepository.save(externalPerson);

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        ScheduleTemplate baseSchedule = new ScheduleTemplate(
                now,
                now.plusHours(2),
                now.plusMinutes(15),
                now.plusHours(1).plusMinutes(30));

        SeatingType seatingType = new SeatingType();
        seatingType.setActive(true);
        seatingType.setName("TEST_SEATINGTYPE");
        seatingType = seatingRepository.save(seatingType);

        seatingType2 = new SeatingType();
        seatingType2.setActive(true);
        seatingType2.setName("TEST_SEATINGTYPE_2");
        seatingType2 = seatingRepository.save(seatingType2);

        Room room = new Room();
        room.setActive(true);
        room.setName("TEST_ROOM");
        room.setNumber("1");

        RoomSeatingCapacity roomSeatingCapacity = new RoomSeatingCapacity();
        roomSeatingCapacity.setSeatingType(seatingType);
        roomSeatingCapacity.setRoom(room);
        roomSeatingCapacity.setCapacity(10);

        RoomSeatingCapacity roomSeatingCapacity2 = new RoomSeatingCapacity();
        roomSeatingCapacity2.setSeatingType(seatingType2);
        roomSeatingCapacity2.setRoom(room);
        roomSeatingCapacity2.setCapacity(20);

        room.setRoomSeatingCapacities(new HashSet<>(Set.of(roomSeatingCapacity, roomSeatingCapacity2)));
        room = roomRepository.save(room);
        room = roomRepository.findWithDetailsById(room.getId()).orElseThrow();

        room2 = new Room();
        room2.setActive(true);
        room2.setName("TEST_ROOM_2");
        room2.setNumber("2");

        RoomSeatingCapacity roomSeatingCapacity3 = new RoomSeatingCapacity();
        roomSeatingCapacity3.setSeatingType(seatingType);
        roomSeatingCapacity3.setRoom(room2);
        roomSeatingCapacity3.setCapacity(10);

        room2.setRoomSeatingCapacities(new HashSet<>(Set.of(roomSeatingCapacity3)));
        room2 = roomRepository.save(room2);
        room2 = roomRepository.findWithDetailsById(room2.getId()).orElseThrow();

        equipment = new Equipment();
        equipment.setActive(true);
        equipment.setName("TEST_EQUIPMENT");
        equipment = equipmentRepository.save(equipment);

        existingBooking = new Booking();
        existingBooking.setTitle("TEST_BOOKING");
        existingBooking.setBookedBy(internalPerson);
        existingBooking.setStatus(BookingStatus.ROOM_APPROVED);
        existingBooking.setBookingType(BookingType.DEFAULT);
        existingBooking.setSchedule(baseSchedule);
        existingBooking.setRoom(room);
        existingBooking.setEquipment(new HashSet<>(Set.of(equipment)));
        existingBooking.setSeatingType(seatingType);
        existingBooking.setParticipantCount(1);
        existingBooking.setCateringNeeded(true);
        existingBooking.setRecurringRule("FREQ=DAILY;COUNT=1");
        existingBooking.setOrganisationUnit("ITM");
        existingBooking = bookingRepository.save(existingBooking);

        baseBooking = new Booking();
        baseBooking.setTitle("TEST_BOOKING");
        baseBooking.setStatus(BookingStatus.NEW);
        baseBooking.setBookingType(BookingType.DEFAULT);
        baseBooking.setSchedule(baseSchedule);

        appointment = new Appointment();
        appointment.setBooking(existingBooking);
        appointment.setSchedule(existingBooking.getSchedule());
        appointment = appointmentRepository.save(appointment);

        ScheduleTemplate scheduleUpdated = new ScheduleTemplate(
                now.minusHours(1),
                now.plusHours(2),
                now.plusMinutes(15),
                now.plusHours(1).plusMinutes(30));
        appointmentUpdate = new Appointment();
        appointmentUpdate.setBooking(existingBooking);
        appointmentUpdate.setSchedule(scheduleUpdated);
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_shouldSetBookedForToSameUser_WhenBookedByAnwenderAndNoBookedFor() {
        Booking result = bookingService.createBooking(baseBooking);

        assertNotNull(result);
        assertThat(result.getBookedFor()).isEqualTo(internalPerson);
        assertThat(result.getBookedBy()).isEqualTo(internalPerson);
        assertThat(result.getOrganisationUnit()).isEqualTo(internalPerson.getOrganisationUnit());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_shouldKeepBookedFor_WhenBookedByAnwender() {
        baseBooking.setBookedFor(externalPerson);
        Booking result = bookingService.createBooking(baseBooking);

        assertNotNull(result);
        assertThat(result.getBookedFor()).isEqualTo(externalPerson);
        assertThat(result.getBookedBy()).isEqualTo(internalPerson);
        assertThat(result.getOrganisationUnit()).isEqualTo(internalPerson.getOrganisationUnit());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000002", authorities = { Roles.RAUM_ADMIN })
    void createBooking_shouldSetBookedByToBookedFor_WhenBookedByRaumadmin() {
        baseBooking.setBookedFor(internalPerson);
        Booking result = bookingService.createBooking(baseBooking);

        assertNotNull(result);
        assertThat(result.getBookedFor()).isEqualTo(internalPerson);
        assertThat(result.getBookedBy()).isEqualTo(internalPerson);
        assertThat(result.getOrganisationUnit()).isEqualTo(internalPerson.getOrganisationUnit());
    }

    @Test
    @WithMockJwt(lhmObjectID = "000002", authorities = { Roles.RAUM_ADMIN })
    void createBooking_shouldSetBookedByToUser_WhenBookedByRaumadminAndBookedForIsExternal() {
        baseBooking.setBookedFor(externalPerson);
        Booking result = bookingService.createBooking(baseBooking);

        assertNotNull(result);
        assertThat(result.getBookedFor()).isEqualTo(externalPerson);
        assertThat(result.getBookedBy()).isEqualTo(internalPersonAdmin);
        assertThat(result.getOrganisationUnit()).isEqualTo(internalPerson.getOrganisationUnit());
    }

    @ParameterizedTest
    @MethodSource("provideBookingChanges")
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void updateBooking_shouldAutomaticallySetStatusToRoomChanged_WhenChangeOccursAndRoleAnwender(
            Consumer<Booking> changeTrigger) {

        Booking bookingUpdate = new Booking();
        bookingUpdate.updateFrom(existingBooking);

        changeTrigger.accept(bookingUpdate);

        Booking result = bookingService.updateBooking(bookingUpdate, existingBooking.getId());

        assertNotNull(result);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.ROOM_CHANGED);
    }

    @ParameterizedTest
    @MethodSource("provideBookingChanges")
    @WithMockJwt(lhmObjectID = "000002", authorities = { Roles.RAUM_ADMIN })
    void updateBooking_shouldKeepStatus_WhenChangeOccursAndRoleRaumadmin(
            Consumer<Booking> changeTrigger) {

        Booking bookingUpdate = new Booking();
        bookingUpdate.updateFrom(existingBooking);

        changeTrigger.accept(bookingUpdate);

        Booking result = bookingService.updateBooking(bookingUpdate, existingBooking.getId());

        assertNotNull(result);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.ROOM_APPROVED);
    }

    private Stream<Arguments> provideBookingChanges() {
        return Stream.of(
                Arguments.of((Consumer<Booking>) b -> b.setRoom(room2)),
                Arguments.of((Consumer<Booking>) b -> {
                    equipment.setName("NEW");
                    b.setEquipment(Set.of(equipment));
                }),
                Arguments.of((Consumer<Booking>) b -> b.setSeatingType(seatingType2)),
                Arguments.of((Consumer<Booking>) b -> b.setParticipantCount(2)),
                Arguments.of((Consumer<Booking>) b -> b.setCateringNeeded(false)),
                Arguments.of((Consumer<Booking>) b -> b.setRecurringRule("FREQ=WEEKLY;COUNT=1")),
                Arguments.of((Consumer<Booking>) b -> {
                    OffsetDateTime later = OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(1);
                    b.setSchedule(
                            new ScheduleTemplate(
                                    later,
                                    later.plusHours(2),
                                    later.plusMinutes(15),
                                    later.plusHours(1).plusMinutes(30)));
                }));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000002", authorities = { Roles.RAUM_ADMIN })
    void updateBooking_shouldThrow_WhenStatusChangedToCANCELEDAndAdditionalChanges() {
        Booking bookingUpdate = new Booking();
        bookingUpdate.updateFrom(existingBooking);
        bookingUpdate.setStatus(BookingStatus.CANCELED);
        bookingUpdate.setTitle("NEW_TITLE");

        assertThrows(BadRequestException.class, () -> bookingService.updateBooking(bookingUpdate, existingBooking.getId()));
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void updateBooking_shouldBeValid_WhenStatusChangedToCANCELEDAndNoAdditionalChanges() {
        Booking bookingUpdate = new Booking();
        existingBooking.setBookedFor(internalPerson);
        existingBooking.setOrganisationUnit("ITM");
        existingBooking = bookingRepository.save(existingBooking);
        bookingUpdate.updateFrom(existingBooking);
        bookingUpdate.setStatus(BookingStatus.CANCELED);

        Booking result = bookingService.updateBooking(bookingUpdate, existingBooking.getId());

        assertNotNull(result);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.CANCELED);
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void bookingStatusChanges_afterAppointmentCommit() {
        TransactionTemplate tx = new TransactionTemplate(txManager);

        tx.executeWithoutResult(status -> appointmentService.updateAppointment(appointmentUpdate, appointment.getId()));

        Booking booking = bookingRepository.findById(appointmentUpdate.getBooking().getId()).orElseThrow();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.ROOM_CHANGED);
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_shouldForceBookingTypeToDefaultAndClearInternalNotes_WhenUserHasNoTerminOrganisatorRole() {
        baseBooking.setBookingType(BookingType.FREE);
        baseBooking.setInternalNotes("some internal note");

        Booking result = bookingService.createBooking(baseBooking);

        assertNotNull(result);
        assertThat(result.getBookingType()).isEqualTo(BookingType.DEFAULT);
        assertThat(result.getInternalNotes()).isNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.NEW);
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.TERMIN_ORGANISATOR })
    void createBooking_shouldKeepBookingTypeAndInternalNotes_WhenUserHasTerminOrganisatorRole() {
        baseBooking.setBookingType(BookingType.FREE);
        baseBooking.setInternalNotes("some internal note");

        Booking result = bookingService.createBooking(baseBooking);

        assertNotNull(result);
        assertThat(result.getBookingType()).isEqualTo(BookingType.FREE);
        assertThat(result.getInternalNotes()).isEqualTo("some internal note");
        assertThat(result.getStatus()).isEqualTo(BookingStatus.ORGANIZER_APPROVED);
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    void createBooking_shouldKeepBookingTypeDefault_WhenUserHasNoTerminOrganisatorRoleAndTypeAlreadyDefault() {
        baseBooking.setBookingType(BookingType.DEFAULT);

        Booking result = bookingService.createBooking(baseBooking);

        assertNotNull(result);
        assertThat(result.getBookingType()).isEqualTo(BookingType.DEFAULT);
        assertThat(result.getInternalNotes()).isNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.NEW);
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.TERMIN_ORGANISATOR })
    @Transactional
    void updateBooking_shouldSetStatusToOrganizerApproved_WhenUserHasTerminOrganisatorRoleAndBookingTypeChangedFromDefault() {
        existingBooking.setBookingType(BookingType.DEFAULT);
        existingBooking.setStatus(BookingStatus.NEW);
        existingBooking = bookingRepository.save(existingBooking);
        Booking bookingUpdate = new Booking();
        bookingUpdate.updateFrom(existingBooking);
        bookingUpdate.setBookingType(BookingType.SERVICE);

        Booking result = bookingService.updateBooking(bookingUpdate, existingBooking.getId());

        assertNotNull(result);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.ORGANIZER_APPROVED);
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.TERMIN_ORGANISATOR })
    @Transactional
    void updateBooking_shouldSetStatusToRoomChanged_WhenUserHasTerminOrganisatorRoleAndBookingTypeChangedBackToDefault() {
        existingBooking.setBookingType(BookingType.FREE);
        existingBooking.setStatus(BookingStatus.ORGANIZER_APPROVED);
        existingBooking = bookingRepository.save(existingBooking);

        baseBooking.updateFrom(existingBooking);
        baseBooking.setBookingType(BookingType.DEFAULT);

        Booking result = bookingService.updateBooking(baseBooking, existingBooking.getId());

        assertNotNull(result);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.ROOM_CHANGED);
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = { Roles.ANWENDER })
    @Transactional
    void updateBooking_shouldNotChangeStatus_WhenBookingTypeStaysDefault() {
        existingBooking.setBookingType(BookingType.DEFAULT);
        existingBooking.setStatus(BookingStatus.ROOM_APPROVED);
        existingBooking = bookingRepository.save(existingBooking);

        baseBooking.updateFrom(existingBooking);
        baseBooking.setBookingType(BookingType.DEFAULT);

        Booking result = bookingService.updateBooking(baseBooking, existingBooking.getId());

        assertNotNull(result);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.ROOM_APPROVED);
    }
}
