package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_OCCUPANCY_END_BEFORE_START;
import static java.time.temporal.ChronoUnit.MINUTES;

import de.muenchen.raumreservierung.common.BadRequestException;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Embeddable
public record ScheduleTemplate(
        @NotNull OffsetDateTime occupancyStart,
        @NotNull OffsetDateTime occupancyEnd,
        OffsetDateTime appointmentStart,
        OffsetDateTime appointmentEnd) implements Serializable {

    public ScheduleTemplate {
        Objects.requireNonNull(occupancyStart);
        Objects.requireNonNull(occupancyEnd);

        final ChronoUnit unit = MINUTES;
        occupancyStart = occupancyStart.truncatedTo(unit);
        occupancyEnd = occupancyEnd.truncatedTo(unit);
        if (appointmentStart != null) {
            appointmentStart = appointmentStart.truncatedTo(unit);
        }
        if (appointmentEnd != null) {
            appointmentEnd = appointmentEnd.truncatedTo(unit);
        }

        if (occupancyEnd.isBefore(occupancyStart)) {
            throw new BadRequestException(MSG_OCCUPANCY_END_BEFORE_START);
        }

        if (appointmentStart == null || appointmentStart.isBefore(occupancyStart) || appointmentStart.isAfter(occupancyEnd)) {
            appointmentStart = occupancyStart;
        }

        if (appointmentEnd == null || appointmentEnd.isAfter(occupancyEnd) || appointmentEnd.isBefore(occupancyStart)) {
            appointmentEnd = occupancyEnd;
        }
    }
}
