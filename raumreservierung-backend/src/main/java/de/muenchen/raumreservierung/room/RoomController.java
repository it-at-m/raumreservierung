package de.muenchen.raumreservierung.room;

import de.muenchen.raumreservierung.room.dto.RoomDetailsResponseDTO;
import de.muenchen.raumreservierung.room.dto.RoomListResponseDTO;
import de.muenchen.raumreservierung.room.dto.RoomMapper;
import de.muenchen.raumreservierung.room.dto.RoomRequestDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

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
    public List<RoomListResponseDTO> getAllRooms() {
        return roomService.findAll().stream().map(roomMapper::toDTO).toList();
    }

    @GetMapping({"/{roomId}"})
    @ResponseStatus(HttpStatus.OK)
    public RoomDetailsResponseDTO getRoom(@PathVariable final UUID roomId) {
        return roomMapper.toDetailsDto(roomService.getById(roomId));
    }

    @Transactional
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomDetailsResponseDTO createRoom(@Valid @RequestBody final RoomRequestDTO roomRequestDTO) {
        return roomMapper
                .toDetailsDto(roomService
                        .createRoom(roomMapper
                                .toEntity(roomRequestDTO)));
    }

    @Transactional
    @PutMapping("/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public RoomDetailsResponseDTO updateRoom(@Valid @RequestBody final RoomRequestDTO roomRequestDTO,
                                             @PathVariable final UUID roomId) {
        return roomMapper
                .toDetailsDto(roomService
                        .updateRoom(roomMapper
                                .toEntity(roomRequestDTO), roomId));
    }

    @DeleteMapping("/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteRoom(@Valid @PathVariable("roomId") final UUID roomId) {
        roomService.deleteRoom(roomId);
    }
}
