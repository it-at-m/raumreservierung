package de.muenchen.raumreservierung.appointment;

import jakarta.persistence.QueryHint;
import org.hibernate.jpa.AvailableHints;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.UUID;
import java.util.function.Function;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID>, JpaSpecificationExecutor<Appointment> {

    @QueryHints(value = {
            @QueryHint(name = AvailableHints.HINT_READ_ONLY, value = "true"),
            @QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = AvailableHints.HINT_FETCH_SIZE, value = "500")
    })
    @EntityGraph(attributePaths = {"booking"})
    <S extends Appointment, R> R findBy(Specification<Appointment> spec, Function<? super SpecificationFluentQuery<S>, R> queryFunction);
}
