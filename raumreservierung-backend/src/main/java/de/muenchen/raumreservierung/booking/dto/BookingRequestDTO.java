package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.booking.types.BookingServiceTime;
import de.muenchen.raumreservierung.booking.types.BookingStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class BookingRequestDTO {
    @NotNull private BookingStatus bookingStatus;
    @NotNull @Size(min = 2, max = 255) private String title;
    private int participantCount;
    private List<UUID> equipmentUUIDs;
    @Size(max = 500) private String specialSeatingRequest;
    private boolean cateringNeeded;
    @Size(max = 10) private List<BookingServiceTime> serviceTimes;
}
