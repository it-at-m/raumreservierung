package de.muenchen.raumreservierung.room;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, UUID> {

    @EntityGraph(attributePaths = { "roomSeatingCapacities", "equipment" })
    @Override
    @NonNull Optional<Room> findById(@NonNull UUID id);
}
