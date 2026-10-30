package de.muenchen.raumreservierung.appointment;

import de.muenchen.raumreservierung.appointment.dto.AppointmentFilterDTO;
import de.muenchen.raumreservierung.booking.BookingStatus;
import de.muenchen.raumreservierung.booking.Booking_;
import de.muenchen.raumreservierung.booking.ScheduleTemplate_;
import de.muenchen.raumreservierung.room.Room_;
import de.muenchen.raumreservierung.seating.SeatingType_;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

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

    public static <T extends Appointment> Specification<T> forFutureSeatingTypeUsage(final UUID seatingTypeId, final UUID roomId) {
        final List<Specification<T>> specificationList = new ArrayList<>();
        specificationList.add(filterForSeatingTypeId(seatingTypeId));
        specificationList.add(filterForOccupancyEndAfter(OffsetDateTime.now()));
        specificationList.add(filterExcludingStatus(BookingStatus.CANCELED, BookingStatus.UNFEASIBLE, BookingStatus.NEW));
        if (roomId != null) {
            specificationList.add(filterForRoomId(roomId));
        }
        return Specification.allOf(specificationList);
    }

    private static <T extends Appointment> Specification<T> filterForRoomId(final UUID roomId) {
        return (root, query, cb) -> cb.equal(root.get(Appointment_.booking).get(Booking_.room).get(Room_.id), roomId);
    }

    private static <T extends Appointment> Specification<T> filterForSeatingTypeId(final UUID seatingTypeId) {
        return (root, query, cb) -> cb.equal(root.get(Appointment_.booking).get(Booking_.seatingType).get(SeatingType_.id), seatingTypeId);
    }

    private static <T extends Appointment> Specification<T> filterExcludingStatus(final BookingStatus... status) {
        return (root, query, cb) -> cb.not(root.get(Appointment_.booking).get(Booking_.status).in(List.of(status)));
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

    private static <T extends Appointment> Specification<T> filterForOccupancyEndAfter(final OffsetDateTime now) {
        return (root, query, cb) -> cb.greaterThan(root.get(Appointment_.schedule).get(ScheduleTemplate_.occupancyEnd), now);
    }

}
