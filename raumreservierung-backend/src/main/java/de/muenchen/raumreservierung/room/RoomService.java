package de.muenchen.raumreservierung.room;

import de.muenchen.raumreservierung.common.ConflictException;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.person.domain.Person;
import de.muenchen.raumreservierung.security.Authorities;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
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
    private final EntityManager entityManager;

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

        final Room savedRoom = roomRepository.saveAndFlush(newRoom);
        Hibernate.initialize(savedRoom.getEquipment());
        Hibernate.initialize(savedRoom.getRoomSeatingCapacities());
        if (savedRoom.getContactPerson() != null) {
            savedRoom.setContactPerson((Person) Hibernate.unproxy(savedRoom.getContactPerson()));
        }

        log.debug("Saved room with id {}", savedRoom.getId());
        return savedRoom;
    }

    @PreAuthorize(Authorities.ROOM_MANAGE)
    public Room updateRoom(final Room roomUpdates, final UUID roomId) {
        final Room existingRoom = getEntityOrThrowException(roomId);
        existingRoom.updateFrom(roomUpdates);

        Room savedRoom = roomRepository.save(existingRoom);

        Hibernate.initialize(savedRoom.getEquipment());
        Hibernate.initialize(savedRoom.getRoomSeatingCapacities());
        if (savedRoom.getContactPerson() != null) {
            savedRoom.setContactPerson((Person) Hibernate.unproxy(savedRoom.getContactPerson()));
        }

        log.debug("Updated room with id {}", existingRoom.getId());
        return savedRoom;
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
