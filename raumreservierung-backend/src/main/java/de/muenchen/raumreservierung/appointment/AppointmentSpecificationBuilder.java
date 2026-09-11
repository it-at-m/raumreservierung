package de.muenchen.raumreservierung.appointment;

import de.muenchen.raumreservierung.appointment.dto.AppointmentFilterDTO;
import de.muenchen.raumreservierung.booking.Booking_;
import de.muenchen.raumreservierung.booking.ScheduleTemplate_;
import de.muenchen.raumreservierung.room.Room_;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public final class AppointmentSpecificationBuilder {

    private AppointmentSpecificationBuilder() {
    }

    public static <T extends Appointment> Specification<T> fromFilter(final AppointmentFilterDTO appointmentFilterDTO) {
        final List<Specification<T>> specificationList = new ArrayList<>();

        if (appointmentFilterDTO.bookingId() != null) {
            specificationList.add(filterForBookingId(appointmentFilterDTO.bookingId()));
        }
        if (appointmentFilterDTO.roomIds() != null && !appointmentFilterDTO.roomIds().isEmpty()) {
            specificationList.add(filterForRoomIds(appointmentFilterDTO.roomIds()));
        }
        final OffsetDateTime start = appointmentFilterDTO.startDate();
        if (start != null) {
            log.debug("Appointment start date: {} - {}", start, start.toLocalDate().atStartOfDay(start.getOffset()).toOffsetDateTime());
            specificationList.add(filterForStartDate(start));
        }
        final OffsetDateTime end = appointmentFilterDTO.endDate();
        if (end != null) {
            log.debug("Appointment start date: {} - {}", end, end.toLocalDate().atStartOfDay(end.getOffset()).toOffsetDateTime());
            specificationList.add(filterForEndDate(end));
        }

        return Specification.allOf(specificationList);
    }

    private static <T extends Appointment> Specification<T> filterForRoomIds(final List<UUID> roomIds) {
        return (root, query, cb) -> root.get(Appointment_.booking).get(Booking_.room).get(Room_.id).in(roomIds);
    }

    private static <T extends Appointment> Specification<T> filterForBookingId(final UUID bookingId) {
        return (root, query, cb) -> cb.equal(root.get(Appointment_.booking).get(Booking_.id), bookingId);
    }

    private static <T extends Appointment> Specification<T> filterForStartDate(final OffsetDateTime start) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(Appointment_.schedule).get(ScheduleTemplate_.occupancyStart), start);
    }

    private static <T extends Appointment> Specification<T> filterForEndDate(final OffsetDateTime end) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(Appointment_.schedule).get(ScheduleTemplate_.occupancyEnd), end);
    }

}
