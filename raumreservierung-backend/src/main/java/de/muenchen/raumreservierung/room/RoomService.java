package de.muenchen.raumreservierung.room;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.raumreservierung.common.BaseEntity;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.equipment.EquipmentRepository;
import de.muenchen.raumreservierung.seating.SeatingRepository;
import de.muenchen.raumreservierung.security.Authorities;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final SeatingRepository seatingRepository;
    private final EquipmentRepository equipmentRepository;

    public List<Room> findAll() {
        final List<Room> allRooms = roomRepository.findAll();
        log.debug("Found {} equipments", allRooms.size());
        return allRooms;
    }

    @PreAuthorize(Authorities.ROOM_MANAGE)
    public Room createRoom(final Room room) {
        final Room roomFilled = getEquipmentsAndSeatingTypes(room);
        final Room savedRoom = roomRepository.save(roomFilled);
        log.debug("Created room with id {}", room.getId());
        return savedRoom;
    }

    private Room getEquipmentsAndSeatingTypes(final Room room) {
        final Room roomCopy = new Room();
        roomCopy.setId(room.getId());
        roomCopy.updateFrom(room);
        roomCopy.setSeatingType(getEntities(room.getSeatingType(), seatingRepository::findById));
        roomCopy.setEquipment(getEntities(room.getEquipment(), equipmentRepository::findById));
        return roomCopy;
    }

    private <T extends BaseEntity> Set<T> getEntities(final Collection<T> collection, final Function<UUID, Optional<T>> findInRepo) {
        return Optional.ofNullable(collection).orElse(Collections.emptySet()).stream().map(item -> findInRepo.apply(item.getId()).orElseThrow())
                .collect(Collectors.toSet());
    }

    @PreAuthorize(Authorities.ROOM_MANAGE)
    public Room updateRoom(final Room room, final UUID roomId) {
        final Room foundRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, roomId)));
        foundRoom.updateFrom(room);
        final Room roomFilled = getEquipmentsAndSeatingTypes(foundRoom);
        log.debug("updated room to {}", roomFilled);
        return roomRepository.save(roomFilled);
    }

    @PreAuthorize(Authorities.ROOM_MANAGE)
    public void deleteRoom(final UUID roomId) {
        log.debug("Deleted room to {}", roomId);
        roomRepository.deleteById(roomId);
    }

}
