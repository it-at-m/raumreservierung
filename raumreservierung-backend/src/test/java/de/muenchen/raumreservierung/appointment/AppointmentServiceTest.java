package de.muenchen.raumreservierung.appointment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private ApplicationEventPublisher publisher;
    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment appointment;
    private Appointment appointmentChanged;
    private UUID bookingId;

    @BeforeEach
    void setup() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        ScheduleTemplate schedule = new ScheduleTemplate(
                now,
                now.plusHours(2),
                now.plusMinutes(15),
                now.plusHours(1).plusMinutes(30));
        ScheduleTemplate scheduleChanged = new ScheduleTemplate(
                now.minusHours(1),
                now.plusHours(2),
                now.plusMinutes(15),
                now.plusHours(1).plusMinutes(30));

        bookingId = UUID.randomUUID();
        Booking booking = new Booking();
        booking.setId(bookingId);

        UUID appointmentId = UUID.randomUUID();
        appointment = new Appointment();
        appointment.setSchedule(schedule);
        appointment.setBooking(booking);
        appointment.setId(appointmentId);

        appointmentChanged = new Appointment();
        appointmentChanged.setSchedule(scheduleChanged);
        appointmentChanged.setBooking(booking);
        appointmentChanged.setId(appointmentId);

    }

    @Test
    void publishesEvent_whenScheduleChanged() {

        when(appointmentRepository.findById(any())).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenReturn(appointment);

        appointmentService.updateAppointment(appointmentChanged, appointment.getId());

        verify(publisher).publishEvent(bookingId);
    }

    @Test
    void doesNotPublishEvent_whenScheduleUnchanged() {

        when(appointmentRepository.findById(any())).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenReturn(appointment);

        appointmentService.updateAppointment(appointment, appointment.getId());

        verify(publisher, never()).publishEvent(any());
    }
}
