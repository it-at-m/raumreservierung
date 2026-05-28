package de.muenchen.raumreservierung.appointment;

import de.muenchen.raumreservierung.appointment.dto.AppointmentFilterDTO;
import de.muenchen.raumreservierung.booking.Booking_;
import de.muenchen.raumreservierung.booking.ScheduleTemplate_;
import de.muenchen.raumreservierung.room.Room_;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public final class AppointmentSpecificationBuilder {

    private AppointmentSpecificationBuilder() {
    }

    public static <T extends Appointment> Specification<T> fromFilter(final AppointmentFilterDTO appointmentFilterDTO) {
        final List<Specification<T>> specificationList = new ArrayList<>();
        final java.time.ZoneId zone = java.time.ZoneId.of("Europe/Berlin");

        if (appointmentFilterDTO.roomId() != null) {
            specificationList.add(filterForRoomId(appointmentFilterDTO.roomId()));
        }
        if (appointmentFilterDTO.startDate() != null) {
            specificationList.add(filterForStartDate(appointmentFilterDTO.startDate().atStartOfDay(zone).toOffsetDateTime()));
        }
        if (appointmentFilterDTO.endDate() != null) {
            specificationList.add(filterForEndDate(appointmentFilterDTO.endDate().atTime(LocalTime.MAX).atZone(zone).toOffsetDateTime()));
        }

        return Specification.allOf(specificationList);
    }

    private static <T extends Appointment> Specification<T> filterForRoomId(final UUID roomId) {
        return (root, query, cb) -> cb.equal(root.get(Appointment_.booking).get(Booking_.room).get(Room_.id), roomId);
    }

    private static <T extends Appointment> Specification<T> filterForStartDate(final OffsetDateTime start) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(Appointment_.schedule).get(ScheduleTemplate_.occupancyStart), start);
    }

    private static <T extends Appointment> Specification<T> filterForEndDate(final OffsetDateTime end) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(Appointment_.schedule).get(ScheduleTemplate_.occupancyEnd), end);
    }

}
