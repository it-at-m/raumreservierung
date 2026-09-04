package de.muenchen.raumreservierung.room;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_CANNOT_DELETE_ACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_CANNOT_DELETE_IN_FUTURE_BOOKING;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.raumreservierung.booking.events.FutureBookingCheckEvent;
import de.muenchen.raumreservierung.booking.events.RemoveRoomFromBookingsEvent;
import de.muenchen.raumreservierung.common.ConflictException;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.file.FileAttachment;
import de.muenchen.raumreservierung.file.FileAttachmentService;
import de.muenchen.raumreservierung.security.Authorities;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final EntityManager entityManager;
    private final FileAttachmentService fileAttachmentService;
    private final ApplicationEventPublisher eventPublisher;

    public Room getById(final UUID roomId) {
        return getEntityOrThrowException(roomId);
    }

    public List<Room> findAll(final boolean onlyActive) {

        final List<Room> allRooms = onlyActive ? roomRepository.findByIsActiveTrue()
                : roomRepository.findAll();
        log.debug("Found {} equipments", allRooms.size());
        return allRooms;
    }

    @PreAuthorize(Authorities.ROOM_MANAGE)
    public Room createRoom(final Room room) {
        final Room newRoom = new Room();
        newRoom.updateFrom(room);

        if (room.getPicture() != null) {
            newRoom.setPicture(room.getPicture());

            fileAttachmentService.attachFileAttachment(room.getPicture().getId());

        }

        final Room savedRoom = roomRepository.saveAndFlush(newRoom);
        entityManager.detach(savedRoom);
        if (savedRoom.getContactPerson() != null) {
            entityManager.detach(savedRoom.getContactPerson());
        }

        log.debug("Saved room with id {}", savedRoom.getId());
        return getEntityOrThrowException(savedRoom.getId());
    }

    @PreAuthorize(Authorities.ROOM_MANAGE)
    public Room updateRoom(final Room roomUpdates, final UUID roomId) {
        final Room existingRoom = getEntityOrThrowException(roomId);
        existingRoom.updateFrom(roomUpdates);

        if (!Objects.equals(existingRoom.getPicture(), roomUpdates.getPicture())) {
            if (existingRoom.getPicture() != null) {
                fileAttachmentService.unAttachFileAttachment(existingRoom.getPicture().getId());
            }

            existingRoom.setPicture(roomUpdates.getPicture());

            if (roomUpdates.getPicture() != null) {
                fileAttachmentService.attachFileAttachment(roomUpdates.getPicture().getId());
            }
        }

        roomRepository.saveAndFlush(existingRoom);
        entityManager.detach(existingRoom);
        if (existingRoom.getContactPerson() != null) {
            entityManager.detach(existingRoom.getContactPerson());
        }

        log.debug("Updated room with id {}", existingRoom.getId());
        return getEntityOrThrowException(existingRoom.getId());
    }

    @PreAuthorize(Authorities.ROOM_MANAGE)
    public void deleteRoom(final UUID roomId) {
        final Room toDelete = getEntityOrThrowException(roomId);

        if (toDelete.isActive()) {
            throw new ConflictException(String.format(MSG_CANNOT_DELETE_ACTIVE, roomId));
        }

        if (existsFutureBookingForRoom(roomId)) {
            throw new ConflictException(String.format(MSG_CANNOT_DELETE_IN_FUTURE_BOOKING, roomId));
        }

        eventPublisher.publishEvent(new RemoveRoomFromBookingsEvent(roomId));

        log.debug("Deleted room to {}", roomId);
        roomRepository.deleteById(roomId);
    }

    /**
     * Returns if a fileAttachment is attached to a room
     *
     * @param fileAttachment the fileAttachment to look for
     * @return if any room is attached to the fileAttachment
     */
    public boolean existsByFileAttachment(final FileAttachment fileAttachment) {
        return roomRepository.existsByPicture(fileAttachment);
    }

    private Room getEntityOrThrowException(final UUID roomId) {
        return roomRepository
                .findWithDetailsById(roomId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, roomId)));
    }

    @PreAuthorize(Authorities.ROOM_READ)
    public int findAbsoluteMaxCapacity() {
        return roomRepository.findFirstByOrderByCapacityDesc().map(Room::getCapacity).orElse(0);
    }

    public boolean existsFutureBookingForRoom(final UUID roomId) {
        final FutureBookingCheckEvent event = new FutureBookingCheckEvent(roomId);
        eventPublisher.publishEvent(event);
        return event.isFutureBookingExists();
    }
}
