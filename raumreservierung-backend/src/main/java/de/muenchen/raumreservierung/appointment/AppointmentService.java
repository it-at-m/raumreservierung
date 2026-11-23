package de.muenchen.raumreservierung.appointment;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.raumreservierung.appointment.dto.AppointmentFilterDTO;
import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.security.Authorities;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.model.Recur;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings({ "PMD.CommentDefaultAccessModifier" })
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final ApplicationEventPublisher publisher;

    @PreAuthorize(Authorities.APPOINTMENT_SELF)
    public Page<Appointment> getAppointmentsByPageableAndFilter(final Pageable pageable, @Valid final AppointmentFilterDTO appointmentFilterDTO) {
        final Specification<Appointment> appointmentSpecification = AppointmentSpecificationBuilder.fromFilter(appointmentFilterDTO);
        final Page<Appointment> filteredAppointments = appointmentRepository.findAll(appointmentSpecification, pageable);
        log.debug("Found {} bookings", filteredAppointments.getTotalElements());
        return filteredAppointments;
    }

    @PreAuthorize(Authorities.APPOINTMENT_SELF)
    @Transactional
    public Appointment updateAppointment(final Appointment appointmentUpdates, final UUID appointmentId) {
        final Appointment existingAppointment = getEntityOrThrowException(appointmentId);
        final boolean appointmentChanged = !Objects.equals(existingAppointment.getSchedule(), appointmentUpdates.getSchedule());

        existingAppointment.updateFrom(appointmentUpdates);
        log.debug("Updated appointment with id {}", existingAppointment.getId());
        final Appointment savedAppointment = appointmentRepository.save(existingAppointment);

        if (appointmentChanged) {
            final UUID bookingId = existingAppointment.getBooking().getId();
            publisher.publishEvent(bookingId);
        }

        return savedAppointment;

    }


    @PreAuthorize(Authorities.APPOINTMENT_SELF)
    public void deleteAppointment(final UUID appointmentId) {
        log.debug("Deleted appointment with id {}", appointmentId);
        appointmentRepository.deleteById(appointmentId);
    }

    private static final ZoneId ZONE = ZoneId.of("Europe/Berlin");
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

        final ZonedDateTime apptStart = base.appointmentStart().atZoneSameInstant(ZONE);
        final ZonedDateTime apptEnd   = base.appointmentEnd().atZoneSameInstant(ZONE);
        final ZonedDateTime occStart  = base.occupancyStart().atZoneSameInstant(ZONE);
        final ZonedDateTime occEnd    = base.occupancyEnd().atZoneSameInstant(ZONE);


        final Duration offsetOccupancyStart = Duration.between(apptStart.toLocalDateTime(), occStart.toLocalDateTime());
        final Duration offsetOccupancyEnd = Duration.between(apptStart.toLocalDateTime(), occEnd.toLocalDateTime());
        final Duration offsetAppointmentEnd = Duration.between(apptStart.toLocalDateTime(), apptEnd.toLocalDateTime());

        final String rrule = parse(booking.getRecurringRule());
        final Recur<ZonedDateTime> recur = new Recur<>(rrule);

        final ZonedDateTime limit = recur.getUntil() != null
                ? recur.getUntil()
                : apptStart.plusYears(1);

        final List<ZonedDateTime> dates = recur.getDates(apptStart, apptStart, limit);

        return dates.stream().map(date -> {

            final LocalDateTime local = date.toLocalDateTime();

            final ScheduleTemplate newSchedule = new ScheduleTemplate(
                    toOffset(local.plus(offsetOccupancyStart)),
                    toOffset(local.plus(offsetOccupancyEnd)),
                    toOffset(local),
                    toOffset(local.plus(offsetAppointmentEnd)));

            final Appointment app = new Appointment();
            app.setBooking(booking);
            app.setSchedule(newSchedule);
            return app;
        }).collect(Collectors.toSet());
    }

    private static OffsetDateTime toOffset(final LocalDateTime local) {
        return local.atZone(ZONE).toOffsetDateTime();
    }

    public List<Appointment> findAppointmentsInRange(final OffsetDateTime rangeStart, final OffsetDateTime rangeEnd) {
        final AppointmentFilterDTO filter = new AppointmentFilterDTO(rangeStart, rangeEnd, null, null);
        return appointmentRepository.findAll(AppointmentSpecificationBuilder.fromFilter(filter));
    }

    public boolean existsFutureAppointmentForRoom(final UUID roomId) {
        final Specification<Appointment> spec = AppointmentSpecificationBuilder.filterForFutureRoomUsage(roomId);
        return appointmentRepository.exists(spec);
    }

    private Appointment getEntityOrThrowException(final UUID appointmentId) {
        return appointmentRepository.findById(appointmentId).orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, appointmentId)));
    }

    private String parse(final String fullLine) {
        return fullLine.regionMatches(true, 0, "RRULE:", 0, 6)
                ? fullLine.substring(6).trim()
                : fullLine;
    }

    boolean existsFutureAppointmentForSeatingTypeAndRoom(final UUID seatingTypeId, final UUID roomId) {
        final Specification<Appointment> spec = AppointmentSpecificationBuilder.forFutureSeatingTypeUsage(seatingTypeId, roomId);
        return appointmentRepository.exists(spec);
    }
}
