package de.muenchen.raumreservierung.booking.types;

import de.muenchen.raumreservierung.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.Serial;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class BookingType extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private boolean isBlocking;

    public void updateFrom(final BookingType bookingType) {
        this.bookingStatus = bookingType.getBookingStatus();
        this.color = bookingType.getColor();
        this.isBlocking = bookingType.isBlocking();
    }
}
