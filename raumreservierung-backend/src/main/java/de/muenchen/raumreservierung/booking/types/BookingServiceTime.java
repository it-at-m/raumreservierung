package de.muenchen.raumreservierung.booking.types;

import de.muenchen.raumreservierung.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BookingServiceTime extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(length = 32, nullable = false)
    @Size(min = 2, max = 32) private String title;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    public void updateServiceTime(final BookingServiceTime bookingServiceTime) {
        this.title = bookingServiceTime.getTitle();
        this.startTime = bookingServiceTime.getStartTime();
        this.endTime = bookingServiceTime.getEndTime();
    }

}