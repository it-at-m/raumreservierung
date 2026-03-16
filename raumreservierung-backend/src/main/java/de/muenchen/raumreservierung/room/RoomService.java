package de.muenchen.raumreservierung.room;

import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.equipment.EquipmentRepository;
import de.muenchen.raumreservierung.seating.SeatingRepository;
import de.muenchen.raumreservierung.seating.SeatingType;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public Room createRoom(final Room room) {
        if (room.getSeatingType() != null) {
            final Set<SeatingType> seatingTypes = room.getSeatingType().stream().map(s -> seatingRepository.findById(s.getId()).orElseThrow())
                    .collect(Collectors.toSet());
            room.setSeatingType(seatingTypes);
        }
        if (room.getEquipment() != null) {
            final Set<Equipment> equipment = room.getEquipment().stream().map(e -> equipmentRepository.findById(e.getId()).orElseThrow())
                    .collect(Collectors.toSet());
            room.setEquipment(equipment);
        }
        final Room savedRoom = roomRepository.save(room);
        log.debug("Created room with id {}", room.getId());
        return savedRoom;
    }

}
