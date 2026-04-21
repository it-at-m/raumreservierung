package de.muenchen.raumreservierung.booking.types;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingTypeRepository extends JpaRepository<BookingType, UUID> {
    Optional<BookingType> findByBookingStatus(BookingStatus bookingStatus);
}
