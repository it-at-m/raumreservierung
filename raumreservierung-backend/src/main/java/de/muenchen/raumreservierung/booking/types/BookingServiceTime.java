package de.muenchen.raumreservierung.booking.types;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class BookingServiceTime implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(length = 32, nullable = false)
    @Size(min = 2, max = 32) private String title;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    public void updateServiceTimeFrom(final BookingServiceTime bookingServiceTime) {
        this.title = bookingServiceTime.getTitle();
        this.startTime = bookingServiceTime.getStartTime();
        this.endTime = bookingServiceTime.getEndTime();
    }

}
