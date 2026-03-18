package de.muenchen.raumreservierung.room;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {

    @EntityGraph(attributePaths = {"seatingType", "equipment"})
    Optional<Room> findById(@NotNull UUID id);
}
