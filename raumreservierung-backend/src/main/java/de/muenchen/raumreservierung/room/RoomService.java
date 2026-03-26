package de.muenchen.raumreservierung.room;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.equipment.EquipmentService;
import de.muenchen.raumreservierung.security.Authorities;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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

    private final EquipmentService equipmentService;

    private final RoomSeatingCapacityService roomSeatingCapacityService;

    public Room getById(final UUID roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, roomId)));
    }

    public List<Room> findAll() {
        final List<Room> allRooms = roomRepository.findAll();
        log.debug("Found {} equipments", allRooms.size());
        return allRooms;
    }

    @PreAuthorize(Authorities.ROOM_MANAGE)
    public Room createRoom(final Room room, final Set<UUID> equipmentIds) {
        if (equipmentIds != null && !equipmentIds.isEmpty()) {
            final Set<Equipment> equipmentSet = equipmentIds.stream()
                    .map(equipmentService::getReferenceById)
                    .collect(Collectors.toSet());
            room.setEquipment(equipmentSet);
        }

        final List<RoomSeatingCapacity> roomSeatingCapacities = roomSeatingCapacityService.fillSeatingCapacities(room.getRoomSeatingCapacities(), room);
        room.updateRoomSeatingCapacityFrom(roomSeatingCapacities);

        return roomRepository.save(room);
    }

    @PreAuthorize(Authorities.ROOM_MANAGE)
    public Room updateRoom(final Room room, final Set<UUID> equipmentIds, final UUID roomId) {
        final Room foundRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, roomId)));

        foundRoom.updateFrom(room);
        // Update to diff updater when sets get larger!
        foundRoom.setEquipment(equipmentIds.stream()
                .map(equipmentService::getReferenceById)
                .collect(Collectors.toSet()));

        final List<RoomSeatingCapacity> roomSeatingCapacities = roomSeatingCapacityService.fillSeatingCapacities(room.getRoomSeatingCapacities(), foundRoom);
        foundRoom.updateRoomSeatingCapacityFrom(roomSeatingCapacities);

        return roomRepository.save(foundRoom);
    }

    @PreAuthorize(Authorities.ROOM_MANAGE)
    public void deleteRoom(final UUID roomId) {
        log.debug("Deleted room to {}", roomId);
        roomRepository.deleteById(roomId);
    }

}
