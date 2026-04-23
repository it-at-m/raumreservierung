package de.muenchen.raumreservierung.booking.types;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public record BookingStatus(
        @Enumerated(EnumType.STRING) BookingState bookingState,
        @Enumerated(EnumType.STRING) BookingSubStatus bookingSubStatus
) {
}
