package de.muenchen.raumreservierung.appointment;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.raumreservierung.appointment.dto.AppointmentFilterDTO;
import de.muenchen.raumreservierung.booking.BookingService;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.security.Authorities;
import jakarta.validation.Valid;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final BookingService bookingService;

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
            bookingService.handleAppointmentChanged(bookingId);
        }

        return savedAppointment;

    }

    @PreAuthorize(Authorities.APPOINTMENT_SELF)
    public void deleteAppointment(final UUID appointmentId) {
        log.debug("Deleted appointment with id {}", appointmentId);
        appointmentRepository.deleteById(appointmentId);
    }

    /**
     * Streams all appointments matching the given filter.
     *
     * @param appointmentFilterDTO the filter to apply.
     * @return a stream of all matching appointments.
     */
    @Transactional(readOnly = true)
    public Stream<Appointment> streamAppointmentsByFilter(final AppointmentFilterDTO appointmentFilterDTO) {
        final Specification<Appointment> appointmentSpecification = AppointmentSpecificationBuilder.fromFilter(appointmentFilterDTO);
        return appointmentRepository.findBy(appointmentSpecification, FluentQuery.FetchableFluentQuery::stream);
    }

    private Appointment getEntityOrThrowException(final UUID appointmentId) {
        return appointmentRepository.findById(appointmentId).orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, appointmentId)));
    }
}
