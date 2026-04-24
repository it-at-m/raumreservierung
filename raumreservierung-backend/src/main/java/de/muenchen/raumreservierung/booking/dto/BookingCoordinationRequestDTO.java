package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.booking.types.BookingServiceTime;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BookingCoordinationRequestDTO extends BookingRequestDTO {
    @Size(max = 500) private String cateringCoordination;
    @Size(max = 2000) private String internalNotes;
    private List<BookingServiceTime> serviceTimes;
}
