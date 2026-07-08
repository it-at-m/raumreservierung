package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.booking.BookingStatus;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@SuppressFBWarnings(value = { "EI_EXPOSE_REP" }, justification = "DTOs are simple data carriers")
public record BookingFilterDTO(
        UUID roomId,
        OffsetDateTime start,
        OffsetDateTime end,
        List<BookingStatus> status) {
}
