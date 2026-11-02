package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.appointment.Appointment;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.fortuna.ical4j.model.Recur;
import org.springframework.stereotype.Service;

@Service
public class RecurrenceExpansionService {

    /**
     * Calculates the individual appointments for a booking based on its schedule
     * and an optional recurrence rule and linking them to the booking.
     *
     * @param booking The booking entity containing the base schedule and recurrence rule.
     * @return A Set of Appointment instances representing the calculated dates.
     */
    public Set<Appointment> generateAndLinkAppointments(final Booking booking) {
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

        final String rrule = parse(booking.getRecurringRule());
        final Recur<OffsetDateTime> recur = new Recur<>(rrule);

        final OffsetDateTime seed = base.appointmentStart();
        final OffsetDateTime limit = recur.getUntil() != null
                ? recur.getUntil()
                : seed.plusYears(1);

        final List<OffsetDateTime> dates = recur.getDates(seed, seed, limit);

        return dates.stream().map(date -> {
            final ScheduleTemplate newSchedule = new ScheduleTemplate(
                    date.plus(offsetOccupancyStart),
                    date.plus(offsetOccupancyEnd),
                    date,
                    date.plus(offsetAppointmentEnd));

            final Appointment app = new Appointment();
            app.setBooking(booking);
            app.setSchedule(newSchedule);
            return app;
        }).collect(Collectors.toSet());
    }

    private String parse(final String fullLine) {
        return fullLine.regionMatches(true, 0, "RRULE:", 0, 6)
                ? fullLine.substring(6).trim()
                : fullLine;
    }
}
