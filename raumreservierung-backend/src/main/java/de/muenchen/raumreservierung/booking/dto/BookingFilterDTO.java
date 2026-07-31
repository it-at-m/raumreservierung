package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.booking.BookingStatus;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@SuppressFBWarnings(value = { "EI_EXPOSE_REP" }, justification = "DTOs are simple data carriers")
public record BookingFilterDTO(
        UUID roomId,
        OffsetDateTime start,
        OffsetDateTime end,
        @Parameter(
                style = ParameterStyle.FORM, explode = Explode.FALSE,
                array = @ArraySchema(schema = @Schema(implementation = BookingStatus.class))
        ) List<BookingStatus> status) {
}
