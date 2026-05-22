package de.muenchen.raumreservierung.appointment;

import java.util.UUID;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID>, JpaSpecificationExecutor<Appointment> {
    @Override
    @NonNull Page<Appointment> findAll(@Nullable Specification<Appointment> specification, @NonNull Pageable pageable);
}
