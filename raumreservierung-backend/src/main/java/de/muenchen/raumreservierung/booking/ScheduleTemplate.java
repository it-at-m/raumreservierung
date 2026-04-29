package de.muenchen.raumreservierung.booking;

import jakarta.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
public record ScheduleTemplate(
        LocalDateTime occupancyStart,
        LocalDateTime occupancyEnd,
        LocalDateTime appointmentStart,
        LocalDateTime appointmentEnd) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
