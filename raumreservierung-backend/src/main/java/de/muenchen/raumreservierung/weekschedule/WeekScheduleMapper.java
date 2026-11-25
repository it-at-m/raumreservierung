package de.muenchen.raumreservierung.weekschedule;

import de.muenchen.raumreservierung.appointment.Appointment;
import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.booking.BookingType;
import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import de.muenchen.raumreservierung.room.Room;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface WeekScheduleMapper {
    ZoneId ZONE = ZoneId.of("Europe/Berlin");
    DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM");

    @Mapping(target = "occupancyTime", source = "schedule", qualifiedByName = "occupancyTime")
    @Mapping(target = "appointmentTime", source = "schedule", qualifiedByName = "appointmentTime")
    @Mapping(target = "title", source = "appointment", qualifiedByName = "prefixTitle")
    BookingMinimalExportDto toBookingMinimalDto(Appointment appointment, @Context LocalDate day);

    @Named("occupancyTime")
    default String occupancyTime(final ScheduleTemplate schedule, @Context final LocalDate day) {
        return timeRangeForDay(schedule.occupancyStart(), schedule.occupancyEnd(), day);
    }

    @Named("appointmentTime")
    default String appointmentTime(final ScheduleTemplate schedule, @Context final LocalDate day) {
        return String.format("(%s)", timeRangeForDay(schedule.appointmentStart(), schedule.appointmentEnd(), day));
    }

    default String timeRangeForDay(final OffsetDateTime start, final OffsetDateTime end,
                                   final LocalDate day) {
        final ZonedDateTime dayStart = day.atStartOfDay(ZONE);
        final ZonedDateTime dayEnd = day.atTime(LocalTime.MAX).atZone(ZONE);
        final ZonedDateTime startLocal = local(start);
        final ZonedDateTime endLocal = local(end);

        final ZonedDateTime from = startLocal.isAfter(dayStart) ? startLocal : dayStart;
        final ZonedDateTime to = endLocal.isBefore(dayEnd) ? endLocal : dayEnd;

        return String.format("%s - %s", from.format(TIME_FORMAT), to.format(TIME_FORMAT));
    }


    @Named("prefixTitle")
    default String prefixTitle(final Appointment a, @Context final LocalDate day) {
        final String title = prefixType(a.getBooking());
        final LocalDate start = startDate(a);
        final LocalDate end = endDate(a);
        if (start.equals(end)) {
            return title;
        }
        final long total = ChronoUnit.DAYS.between(start, end) + 1;
        final long dayNo = ChronoUnit.DAYS.between(start, day) + 1;
        return String.format("(%d/%d) %s", dayNo, total, title);
    }

    default String prefixType(final Booking booking) {
        final String title = booking.getTitle() == null ? "" : booking.getTitle();
        final BookingType type = booking.getBookingType();

        return type == null || type.getLabel().isEmpty()
                ? title
                : String.join(": ", type.getLabel(), title);
    }

    default WeekScheduleView toView(final LocalDate monday,
                                    final WeekScheduleCategory category,
                                    final List<RoomRowExportDto> roomRows) {
        return new WeekScheduleView(
                monday.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR),
                monday.getYear(),
                category.getLabel(),
                toDayHeaders(monday),
                roomRows);
    }

    default List<DayHeaderExportDto> toDayHeaders(final LocalDate monday) {
        return Arrays.stream(DayOfWeek.values())
                .map(monday::with)
                .map(day -> new DayHeaderExportDto(
                        day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.GERMAN),
                        day.format(DATE_FORMAT)))
                .toList();
    }

    default RoomRowExportDto toRoomRow(final Room room, final List<Appointment> appointments,
                                       final LocalDate monday) {
        final List<List<BookingMinimalExportDto>> days = IntStream.range(0, 7)
                .mapToObj(monday::plusDays)
                .map(day -> appointments.stream()
                        .filter(a -> !day.isBefore(startDate(a)) && !day.isAfter(endDate(a)))
                        .map(a -> toBookingMinimalDto(a, day))
                        .toList())
                .toList();
        return new RoomRowExportDto(room.getName(), room.getNumber(), days);
    }

    default LocalDate startDate(final Appointment a) {
        return local(a.getSchedule().occupancyStart()).toLocalDate();
    }

    default LocalDate endDate(final Appointment a) {
        return local(a.getSchedule().occupancyEnd()).toLocalDate();
    }

    default ZonedDateTime local(final OffsetDateTime dateTime) {
        return dateTime.atZoneSameInstant(ZONE);
    }
}
