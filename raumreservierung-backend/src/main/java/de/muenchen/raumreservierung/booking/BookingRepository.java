package de.muenchen.raumreservierung.booking;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    @Override
    @NonNull @Transactional(readOnly = true)
    @EntityGraph(attributePaths = { "equipment", "appointments", "room", "contactPerson" })
    List<Booking> findAll();

    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = { "equipment", "appointments", "room", "contactPerson" })
    List<Booking> findByContactPersonEmail(String email);

    @Override
    @NonNull @Transactional(readOnly = true)
    @EntityGraph(attributePaths = { "equipment", "appointments", "room", "contactPerson" })
    Optional<Booking> findById(@NonNull UUID uuid);
}
