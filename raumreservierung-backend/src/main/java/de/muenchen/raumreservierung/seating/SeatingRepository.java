package de.muenchen.raumreservierung.seating;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatingRepository extends JpaRepository<SeatingType, UUID> {
}
