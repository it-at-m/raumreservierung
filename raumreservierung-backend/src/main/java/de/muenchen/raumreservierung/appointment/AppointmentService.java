package de.muenchen.raumreservierung.appointment;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.raumreservierung.appointment.dto.AppointmentFilterDTO;
import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.security.Authorities;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.model.Recur;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    @PreAuthorize(Authorities.USERS_MANAGE)
    public List<Appointment> getAppointmentsByPeriodAndRoom(final AppointmentFilterDTO appointmentFilterDTO) {
        final UUID roomId = appointmentFilterDTO.roomId();
        final LocalDateTime start = appointmentFilterDTO.startDate().atStartOfDay();
        final LocalDateTime end = appointmentFilterDTO.endDate().atTime(java.time.LocalTime.MAX);
        return appointmentRepository.findAllByBookingRoomIdAndScheduleOccupancyStartBetween(roomId, start, end);
    }

    // following three necessary? can also be done via BookingController
    @PreAuthorize(Authorities.APPOINTMENT_WRITE)
    public Appointment createAppointment(final Appointment appointment) {
        log.debug("Creating appointment {}", appointment);
        return appointmentRepository.save(appointment);
    }

    @PreAuthorize(Authorities.APPOINTMENT_WRITE)
    public Appointment updateAppointment(final Appointment appointmentUpdates, final UUID appointmentId) {
        final Appointment existingAppointment = getEntityOrThrowException(appointmentId);
        existingAppointment.updateFrom(appointmentUpdates);
        log.debug("Updated appointment with id {}", existingAppointment.getId());
        return appointmentRepository.save(existingAppointment);
    }

    @PreAuthorize(Authorities.APPOINTMENT_WRITE)
    public void deleteAppointment(final UUID appointmentId) {
        log.debug("Deleted appointment with id {}", appointmentId);
        appointmentRepository.deleteById(appointmentId);
    }

    /**
     * Calculates the individual appointments for a booking based on its schedule
     * and an optional recurrence rule (RRULE).
     * The method handles two distinct scenarios:
     * - Single Appointment: If no recurringRule is provided, it returns
     * a single appointment using the booking's original schedule.
     * - Recurring Series: If a valid iCalendar RRULE is present, it calculates
     * all occurrences within a safety limit (default: 1 year from start).
     * For recurring series, the method preserves the relative time offsets for
     * setup (occupancy start), teardown (occupancy end), and total duration
     * by calculating their distance from the primary appointmentStart and
     * applying them to each generated occurrence.
     *
     * @param booking The booking entity containing the base schedule and recurrence rule.
     * @return A Set of Appointment instances representing the calculated dates.
     */
    public Set<Appointment> calculate(final Booking booking) {
        final ScheduleTemplate base = booking.getSchedule();

        if (booking.getRecurringRule() == null || booking.getRecurringRule().isBlank()) {
            final Appointment app = new Appointment();
            app.setBooking(booking);
            app.setSchedule(base);
            return Set.of(app);
        }

        final Duration offsetOccupancyStart = Duration.between(base.appointmentStart(), base.occupancyStart());
        final Duration offsetOccupancyEnd = Duration.between(base.appointmentStart(), base.occupancyEnd());
        final Duration offsetAppointmentEnd = Duration.between(base.appointmentStart(), base.appointmentEnd());

        final Recur<LocalDateTime> recur = new Recur<>(booking.getRecurringRule());

        final LocalDateTime seed = base.appointmentStart();
        final LocalDateTime limit = seed.plusYears(1);

        final List<LocalDateTime> dates = recur.getDates(seed, seed, limit);

        return dates.stream().map(date -> {
            final ScheduleTemplate newSchedule = new ScheduleTemplate(
                    date.plus(offsetOccupancyStart), // occupancyStart
                    date.plus(offsetOccupancyEnd), // occupancyEnd
                    date, // appointmentStart
                    date.plus(offsetAppointmentEnd) // appointmentEnd
            );

            final Appointment app = new Appointment();
            app.setBooking(booking);
            app.setSchedule(newSchedule);
            return app;
        }).collect(Collectors.toSet());
    }

    private Appointment getEntityOrThrowException(final UUID appointmentId) {
        return appointmentRepository.findById(appointmentId).orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, appointmentId)));
    }
}
