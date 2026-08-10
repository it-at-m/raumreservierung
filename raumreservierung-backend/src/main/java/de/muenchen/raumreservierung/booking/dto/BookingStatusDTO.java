package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.booking.BookingStatus;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@SuppressFBWarnings(value = { "EI_EXPOSE_REP" }, justification = "DTOs are simple data carriers")
public record BookingStatusDTO(
        @NotNull BookingStatus currentStatus,
        @NotNull List<BookingStatus> nextPossibleStatus) {
}
