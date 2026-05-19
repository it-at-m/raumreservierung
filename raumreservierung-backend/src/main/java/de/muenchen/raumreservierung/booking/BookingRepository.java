package de.muenchen.raumreservierung.booking;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookingRepository extends JpaRepository<Booking, UUID>, JpaSpecificationExecutor<Booking> {
    @Override
    @NonNull @EntityGraph(attributePaths = { "appointments", "equipment", "bookedBy", "bookedFor", "room" })
    Page<Booking> findAll(@Nullable Specification<Booking> spec, @NonNull Pageable pageable);

    @Override
    @NonNull @EntityGraph(attributePaths = { "appointments", "equipment", "bookedBy", "bookedFor", "room" })
    Optional<Booking> findById(@NonNull UUID uuid);

    @Override
    @NonNull @EntityGraph(attributePaths = { "appointments", "equipment", "bookedBy", "bookedFor", "room" })
    <S extends Booking> S saveAndFlush(@NonNull S entity);
}
