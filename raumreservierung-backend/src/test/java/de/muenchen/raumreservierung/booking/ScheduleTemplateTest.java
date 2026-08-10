package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_OCCUPANCY_END_BEFORE_START;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.muenchen.raumreservierung.common.BadRequestException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

class ScheduleTemplateTest {

    private static final OffsetDateTime OCCUPANCY_START = OffsetDateTime.of(2024, 1, 1, 8, 0, 30, 500_000_000, ZoneOffset.UTC);
    private static final OffsetDateTime OCCUPANCY_END = OffsetDateTime.of(2024, 1, 1, 18, 0, 45, 123_000_000, ZoneOffset.UTC);

    @Test
    void constructor_truncatesAllFieldsToMinutes() {
        final ScheduleTemplate template = new ScheduleTemplate(
                OCCUPANCY_START,
                OCCUPANCY_END,
                OCCUPANCY_START.plusMinutes(30).plusSeconds(15),
                OCCUPANCY_END.minusMinutes(30).plusSeconds(45));

        assertEquals(0, template.occupancyStart().getSecond());
        assertEquals(0, template.occupancyStart().getNano());
        assertEquals(0, template.occupancyEnd().getSecond());
        assertEquals(0, template.occupancyEnd().getNano());
        assertEquals(0, template.appointmentStart().getSecond());
        assertEquals(0, template.appointmentStart().getNano());
        assertEquals(0, template.appointmentEnd().getSecond());
        assertEquals(0, template.appointmentEnd().getNano());
    }

    @ParameterizedTest
    @MethodSource("provideNullArgumentsData")
    void constructor_nullRequiredField_throwsNullPointerException(OffsetDateTime occupancyStart, OffsetDateTime occupancyEnd) {
        assertThrows(NullPointerException.class,
                () -> new ScheduleTemplate(occupancyStart, occupancyEnd, null, null));
    }

    private static Stream<Arguments> provideNullArgumentsData() {
        return Stream.of(
                Arguments.of(null, OCCUPANCY_END),
                Arguments.of(OCCUPANCY_START, null));
    }

    @Test
    void constructor_occupancyEndBeforeOccupancyStart_throwsBadRequestException() {
        final BadRequestException exception = assertThrows(BadRequestException.class,
                () -> new ScheduleTemplate(OCCUPANCY_END, OCCUPANCY_START, null, null));

        assertEquals(HttpStatus.BAD_REQUEST + " \"" + MSG_OCCUPANCY_END_BEFORE_START + "\"", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideAppointmentStartData")
    void constructor_appointmentStart_resolvesToExpectedValue(
            Function<ScheduleTemplateTest, OffsetDateTime> appointmentStartProvider, boolean expectDefaultToOccupancyStart) {
        final OffsetDateTime appointmentStart = appointmentStartProvider.apply(this);

        final ScheduleTemplate template = new ScheduleTemplate(OCCUPANCY_START, OCCUPANCY_END, appointmentStart, null);

        final OffsetDateTime expected = expectDefaultToOccupancyStart
                ? template.occupancyStart()
                : appointmentStart.truncatedTo(MINUTES);
        assertEquals(expected, template.appointmentStart());
    }

    private static Stream<Arguments> provideAppointmentStartData() {
        return Stream.of(
                // null -> defaults to occupancyStart
                Arguments.of((Function<ScheduleTemplateTest, OffsetDateTime>) t -> null, true),
                // before occupancyStart -> defaults to occupancyStart
                Arguments.of((Function<ScheduleTemplateTest, OffsetDateTime>) t -> OCCUPANCY_START.minusHours(1), true),
                // after occupancyEnd -> defaults to occupancyStart
                Arguments.of((Function<ScheduleTemplateTest, OffsetDateTime>) t -> OCCUPANCY_END.plusHours(1), true),
                // equals occupancyStart -> valid boundary, kept
                Arguments.of((Function<ScheduleTemplateTest, OffsetDateTime>) t -> OCCUPANCY_START, false),
                // within range -> kept
                Arguments.of((Function<ScheduleTemplateTest, OffsetDateTime>) t -> OCCUPANCY_START.plusHours(1), false));
    }

    @ParameterizedTest
    @MethodSource("provideAppointmentEndData")
    void constructor_appointmentEnd_resolvesToExpectedValue(
            Function<ScheduleTemplateTest, OffsetDateTime> appointmentEndProvider, boolean expectDefaultToOccupancyEnd) {
        final OffsetDateTime appointmentEnd = appointmentEndProvider.apply(this);

        final ScheduleTemplate template = new ScheduleTemplate(OCCUPANCY_START, OCCUPANCY_END, null, appointmentEnd);

        final OffsetDateTime expected = expectDefaultToOccupancyEnd
                ? template.occupancyEnd()
                : appointmentEnd.truncatedTo(MINUTES);
        assertEquals(expected, template.appointmentEnd());
    }

    private static Stream<Arguments> provideAppointmentEndData() {
        return Stream.of(
                // null -> defaults to occupancyEnd
                Arguments.of((Function<ScheduleTemplateTest, OffsetDateTime>) t -> null, true),
                // after occupancyEnd -> defaults to occupancyEnd
                Arguments.of((Function<ScheduleTemplateTest, OffsetDateTime>) t -> OCCUPANCY_END.plusHours(1), true),
                // before occupancyStart -> defaults to occupancyEnd
                Arguments.of((Function<ScheduleTemplateTest, OffsetDateTime>) t -> OCCUPANCY_START.minusHours(1), true),
                // equals occupancyEnd -> valid boundary, kept
                Arguments.of((Function<ScheduleTemplateTest, OffsetDateTime>) t -> OCCUPANCY_END, false),
                // within range -> kept
                Arguments.of((Function<ScheduleTemplateTest, OffsetDateTime>) t -> OCCUPANCY_END.minusHours(1), false));
    }
}
