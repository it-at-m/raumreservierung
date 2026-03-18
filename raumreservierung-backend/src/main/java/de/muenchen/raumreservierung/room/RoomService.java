package de.muenchen.raumreservierung.room;

import de.muenchen.raumreservierung.common.BaseEntity;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.equipment.EquipmentRepository;
import de.muenchen.raumreservierung.equipment.EquipmentService;
import de.muenchen.raumreservierung.seating.SeatingRepository;
import de.muenchen.raumreservierung.seating.SeatingService;
import de.muenchen.raumreservierung.security.Authorities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;


    private final SeatingService seatingService;
    private final EquipmentService equipmentService;
    private final SeatingRepository seatingRepository;
    private final EquipmentRepository equipmentRepository;

    public Room getById(UUID roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, roomId)));
    }

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

    @PreAuthorize(Authorities.ROOM_MANAGE)
    public Room createRoom(final Room room, Set<UUID> equipmentIds) {
        if (equipmentIds != null && !equipmentIds.isEmpty()) {
            Set<Equipment> equipmentSet = equipmentIds.stream()
                    .map(equipmentService::getReferenceById)
                    .collect(Collectors.toSet());
            room.setEquipment(equipmentSet);
        }

        return roomRepository.save(room);
    }

    @PreAuthorize(Authorities.ROOM_MANAGE)
    public Room updateRoom(final Room room, Set<UUID> equipmentIds, UUID roomId) {
        final Room foundRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, roomId)));

        foundRoom.updateFrom(room);
        // Update to diff updater when sets get larger!
        foundRoom.setEquipment(equipmentIds.stream()
                .map(equipmentService::getReferenceById)
                .collect(Collectors.toSet()));

        return roomRepository.save(foundRoom);
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
