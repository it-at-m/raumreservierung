package de.muenchen.raumreservierung.booking.types.appointment;

import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import java.io.Serial;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Appointment extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private LocalDateTime start;

    @Column(nullable = false)
    private LocalDateTime end;

    @Column(nullable = false)
    private int duration;

    @ManyToOne
    private Booking booking;

    //    @ElementCollection
    //    private List<BookingServiceTime> serviceTimes = new ArrayList<>();

}
