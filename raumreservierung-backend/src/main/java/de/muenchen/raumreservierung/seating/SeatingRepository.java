package de.muenchen.raumreservierung.seating;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SeatingRepository extends JpaRepository<SeatingType, UUID> {
}
