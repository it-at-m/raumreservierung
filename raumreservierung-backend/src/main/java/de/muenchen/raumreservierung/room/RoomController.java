package de.muenchen.raumreservierung.room;

import de.muenchen.raumreservierung.room.dto.RoomMapper;
import de.muenchen.raumreservierung.room.dto.RoomRequestDTO;
import de.muenchen.raumreservierung.room.dto.RoomResponseDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    private final RoomMapper roomMapper;

    @Transactional
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RoomResponseDTO> getAllRooms() {
        return roomService.findAll().stream().map(roomMapper::toDTO).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponseDTO createRoom(@Valid @RequestBody final RoomRequestDTO roomRequestDTO) {
        return roomMapper.toDTO(roomService.createRoom(roomMapper.toEntity(roomRequestDTO)));
    }
}
