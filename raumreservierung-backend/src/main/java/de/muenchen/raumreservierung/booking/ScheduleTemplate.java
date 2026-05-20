package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.common.BadRequestException;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public record ScheduleTemplate(
        @NotNull LocalDateTime occupancyStart,
        @NotNull LocalDateTime occupancyEnd,
        LocalDateTime appointmentStart,
        LocalDateTime appointmentEnd) implements Serializable {

    private static final String ERROR_OCCUPANCY_END_BEFORE_START = "occupancyEnd cannot be before occupancyStart";

    public ScheduleTemplate {
        Objects.requireNonNull(occupancyStart);
        Objects.requireNonNull(occupancyEnd);

        if (occupancyEnd.isBefore(occupancyStart)) {
            throw new BadRequestException(ERROR_OCCUPANCY_END_BEFORE_START);
        }

        if (appointmentStart == null || appointmentStart.isBefore(occupancyStart) || appointmentStart.isAfter(occupancyEnd)) {
            appointmentStart = occupancyStart;
        }

        if (appointmentEnd == null || appointmentEnd.isAfter(occupancyEnd) || appointmentEnd.isBefore(occupancyStart)) {
            appointmentEnd = occupancyEnd;
        }
    }
}
