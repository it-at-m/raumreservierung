package de.muenchen.raumreservierung.room;

import de.muenchen.raumreservierung.common.ConflictException;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.equipment.EquipmentService;
import de.muenchen.raumreservierung.security.Authorities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_CANNOT_DELETE_ACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    private final EquipmentService equipmentService;

    private final RoomSeatingCapacityService roomSeatingCapacityService;

    public Room getById(final UUID roomId) {
        return roomRepository.findWithDetailsById(roomId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, roomId)));
    }

    public List<Room> findAll() {
        final List<Room> allRooms = roomRepository.findAll();
        log.debug("Found {} equipments", allRooms.size());
        return allRooms;
    }

    @PreAuthorize(Authorities.ROOM_MANAGE)
    public Room createRoom(final Room room) {
        return roomRepository.save(room);
    }

    @PreAuthorize(Authorities.ROOM_MANAGE)
    public Room updateRoom(final Room roomUpdates, final UUID roomId) {
        final Room existingRoom = getEntityOrThrowException(roomId);

        existingRoom.updateFrom(roomUpdates);

        return roomRepository.save(existingRoom);
    }

    @PreAuthorize(Authorities.ROOM_MANAGE)
    public void deleteRoom(final UUID roomId) {
        final Room toDelete = getEntityOrThrowException(roomId);

        if (toDelete.getIsActive()) {
            throw new ConflictException(String.format(MSG_CANNOT_DELETE_ACTIVE, roomId));
        }

        log.debug("Deleted room to {}", roomId);
        roomRepository.deleteById(roomId);
    }

    private Room getEntityOrThrowException(final UUID roomId) {
        return roomRepository
                .findWithDetailsById(roomId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, roomId)));
    }

}
