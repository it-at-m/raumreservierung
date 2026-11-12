package de.muenchen.raumreservierung.weekschedule;

import de.muenchen.raumreservierung.appointment.Appointment;
import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.booking.BookingType;
import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import de.muenchen.raumreservierung.room.Room;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface WeekScheduleMapper {

    int DAYS = 7;

    DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM");
    DateTimeFormatter DAY_TIME_FORMAT = DateTimeFormatter.ofPattern("EE HH:mm", Locale.GERMAN);


    @Mapping(target = "occupancyTime", source = "schedule", qualifiedByName = "occupancyTime")
    @Mapping(target = "appointmentTime", source = "schedule", qualifiedByName = "appointmentTime")
    @Mapping(target = "title", source = "booking", qualifiedByName = "prefixType")
    BookingMinimalExportDto toBookingMinimalDto(Appointment appointment);

    @Named("occupancyTime")
    default String occupancyTime(final ScheduleTemplate schedule) {
        return String.format("%s - %s",
                schedule.occupancyStart().format(TIME_FORMAT),
                schedule.occupancyEnd().format(TIME_FORMAT));
    }

    @Named("appointmentTime")
    default String appointmentTime(final ScheduleTemplate schedule) {
        return String.format("(%s - %s)",
                schedule.appointmentStart().format(TIME_FORMAT),
                schedule.appointmentEnd().format(TIME_FORMAT));
    }

    @Named("prefixType")
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

    default BookingMinimalExportDto toMultiDayDto(final Appointment appointment) {
        final BookingMinimalExportDto dto = toBookingMinimalDto(appointment);
        return new BookingMinimalExportDto(
                spanLabel(appointment.getSchedule()),
                dto.appointmentTime(),
                dto.title());
    }

    default String spanLabel(final ScheduleTemplate schedule) {
        return String.format("%s - %s",
                schedule.occupancyStart().atZoneSameInstant(ZoneId.systemDefault()).format(DAY_TIME_FORMAT),
                schedule.occupancyEnd().atZoneSameInstant(ZoneId.systemDefault()).format(DAY_TIME_FORMAT));
    }

    default RoomRowExportDto toRoomRow(final Room room, final List<Appointment> appointments,
                                       final LocalDate monday) {
        return new RoomRowExportDto(room.getName(), room.getNumber(), toLanes(appointments, monday));
    }

    default List<List<LaneCellDto>> toLanes(final List<Appointment> appointments, final LocalDate monday) {
        final List<Slot[]> lanes = new ArrayList<>();
        final List<DaySpan> spans = new ArrayList<>();
        final Map<Integer, List<Appointment>> singlesByDay = new TreeMap<>();

        for (final Appointment a : appointments) {
            final LocalDate start = startDate(a);
            final LocalDate end = endDate(a);
            final long rawStart = ChronoUnit.DAYS.between(monday, start);
            final long rawEnd = ChronoUnit.DAYS.between(monday, end);

            if (start.equals(end)) {
                if (rawStart >= 0 && rawStart < DAYS) {
                    singlesByDay.computeIfAbsent((int) rawStart, d -> new ArrayList<>()).add(a);
                }
            } else {
                spans.add(new DaySpan(a,
                        (int) Math.max(0, rawStart),
                        (int) Math.min(DAYS - 1, rawEnd),
                        rawStart < 0,
                        rawEnd > DAYS - 1));
            }
        }

        // 1. Mehrtägige Termine verteilen: früheste zuerst, bei gleichem Start die längsten
        spans.stream()
                .sorted(Comparator.comparingInt(DaySpan::startIdx)
                        .thenComparing(s -> s.startIdx() - s.endIdx())
                        .thenComparing(s -> s.appointment().getSchedule().occupancyStart()))
                .forEach(s -> {
                    final Slot[] lane = findLane(lanes, s.startIdx(), s.endIdx());
                    for (int d = s.startIdx(); d <= s.endIdx(); d++) {
                        lane[d].span = s;
                    }
                });

        // 2. Eintägige Termine in die oberste Lane, die an diesem Tag frei ist
        singlesByDay.forEach((day, list) -> {
            final Slot[] lane = findLane(lanes, day, day);
            list.stream()
                    .sorted(Comparator.comparing(a -> a.getSchedule().occupancyStart()))
                    .map(this::toBookingMinimalDto)
                    .forEach(lane[day].singles::add);
        });

        // Raum ohne Termine: eine leere Lane, damit die Zeile trotzdem erscheint
        if (lanes.isEmpty()) {
            lanes.add(newLane());
        }

        // 3. Lanes in Zellen umwandeln
        return lanes.stream().map(this::toCells).toList();
    }

    /** Erste Lane, in der die Tage start..end frei sind, sonst eine neue. */
    default Slot[] findLane(final List<Slot[]> lanes, final int start, final int end) {
        final boolean isSpan = start != end;
        for (final Slot[] lane : lanes) {
            boolean free = true;
            for (int d = start; d <= end && free; d++) {
                free = lane[d].span == null && (!isSpan || lane[d].singles.isEmpty());
            }
            if (free) {
                return lane;
            }
        }
        final Slot[] lane = newLane();
        lanes.add(lane);
        return lane;
    }

    default Slot[] newLane() {
        final Slot[] lane = new Slot[DAYS];
        for (int d = 0; d < DAYS; d++) {
            lane[d] = new Slot();
        }
        return lane;
    }

    /** Zusammenhängende Tage einer Spanne werden zu einer Zelle mit colspan, alle anderen Tage einzeln. */
    default List<LaneCellDto> toCells(final Slot[] lane) {
        final List<LaneCellDto> cells = new ArrayList<>();
        int d = 0;
        while (d < DAYS) {
            final DaySpan s = lane[d].span;
            if (s != null) {
                cells.add(new LaneCellDto(
                        s.endIdx() - s.startIdx() + 1,
                        true,
                        s.continuesBefore(),
                        s.continuesAfter(),
                        List.of(toMultiDayDto(s.appointment()))));
                d = s.endIdx() + 1;
            } else {
                cells.add(new LaneCellDto(1, false, false, false, List.copyOf(lane[d].singles)));
                d++;
            }
        }
        return cells;
    }

    // ------------------------------------------------------------------
    // Datumshilfen
    // ------------------------------------------------------------------

    default LocalDate startDate(final Appointment a) {
        return a.getSchedule().occupancyStart()
                .atZoneSameInstant(ZoneId.systemDefault())
                .toLocalDate();
    }

    /** Endet ein Termin genau um 00:00, zählt der Folgetag nicht mehr dazu. */
    default LocalDate endDate(final Appointment a) {
        return a.getSchedule().occupancyEnd().minusNanos(1)
                .atZoneSameInstant(ZoneId.systemDefault())
                .toLocalDate();
    }

    // ------------------------------------------------------------------
    // Interne Hilfstypen
    // ------------------------------------------------------------------

    record DaySpan(Appointment appointment, int startIdx, int endIdx,
                   boolean continuesBefore, boolean continuesAfter) { }

    /** Ein Tag in einer Lane: entweder von einer Spanne belegt oder Sammelstelle für eintägige Termine. */
    final class Slot {
        DaySpan span;
        final List<BookingMinimalExportDto> singles = new ArrayList<>();
    }
}
