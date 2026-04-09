package de.muenchen.raumreservierung.room;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_CANNOT_DELETE_ACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;
import de.muenchen.raumreservierung.common.ConflictException;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.security.Authorities;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

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
        final Room newRoom = new Room();
        newRoom.updateFrom(room);
        return roomRepository.save(newRoom);
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

        if (toDelete.isActive()) {
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
