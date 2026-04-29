package de.muenchen.raumreservierung.appointment;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.raumreservierung.appointment.dto.AppointmentFilterDTO;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.security.Authorities;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private Appointment getEntityOrThrowException(final UUID appointmentId) {
        return appointmentRepository.findById(appointmentId).orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, appointmentId)));
    }
}
