package de.muenchen.raumreservierung.room;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RoomRepository extends JpaRepository<Room, UUID> {

    @EntityGraph(attributePaths = { "roomSeatingCapacities", "roomSeatingCapacities.seatingType", "equipment", "contactPerson" })
    @NonNull @Transactional(readOnly = true)
    Optional<Room> findWithDetailsById(@NonNull UUID id);

    List<Room> findByIsActiveTrue();

}
