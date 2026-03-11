package de.muenchen.raumreservierung.room;

import de.muenchen.raumreservierung.room.dto.RoomMapper;
import de.muenchen.raumreservierung.room.dto.RoomRequestDTO;
import de.muenchen.raumreservierung.room.dto.RoomResponseDTO;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    private final RoomMapper roomMapper;

    @Transactional
    public List<RoomResponseDTO> findAll() {
        final List<RoomResponseDTO> allRooms = roomRepository.findAll().stream().map(roomMapper::toDTO).toList();
        log.debug("Found {} equipments", allRooms.size());
        return allRooms;
    }

    public RoomResponseDTO createRoom(RoomRequestDTO roomRequestDTO) {
        final Room room = roomMapper.toEntity(roomRequestDTO);
        final Room savedRoom = roomRepository.save(room);
        final RoomResponseDTO roomResponseDTO = roomMapper.toDTO(savedRoom);
        log.debug("Created room with id {}", room.getId());
        return roomResponseDTO;
    }
}
