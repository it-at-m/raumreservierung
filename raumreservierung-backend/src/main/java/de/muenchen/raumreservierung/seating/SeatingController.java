package de.muenchen.raumreservierung.seating;

import de.muenchen.raumreservierung.seating.dto.SeatingTypeMapper;
import de.muenchen.raumreservierung.seating.dto.SeatingTypeRequestDto;
import de.muenchen.raumreservierung.seating.dto.SeatingTypeResponseDto;
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
@RequestMapping("/seating")
public class SeatingController {

    private final SeatingService SeatingService;

    private final SeatingTypeMapper seatingTypeMapper;

    /**
     * Retrieve all Seating entities.
     * Returns a list of all existing Seating resources.
     *
     * @return list of Seating as response DTOs
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SeatingTypeResponseDto> getAllSeatingTypes() {
        return SeatingService.findAll().stream().map(seatingTypeMapper::toDto).toList();
    }

    /**
     * Create a new Seating entity.
     * Creates a new Seating resource using the provided details.
     *
     * @param seating the details of the Seating to create
     * @return the created Seating as response DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SeatingTypeResponseDto createSeatingType(@Valid @RequestBody final SeatingTypeRequestDto seating) {
        return seatingTypeMapper.toDto(SeatingService.createSeating(seatingTypeMapper.toEntity(seating)));
    }

    /**
     * Update an existing Seating entity.
     * Updates an existing Seating resource using the provided details.
     *
     * @param seating the updated Seating details
     * @return the updated Seating as response DTO
     */
    @PutMapping("/{seatingId}")
    @ResponseStatus(HttpStatus.OK)
    public SeatingTypeResponseDto updateSeatingType(@Valid @RequestBody final SeatingTypeRequestDto seating, @PathVariable("seatingId") final UUID seatingId) {
        return seatingTypeMapper.toDto(SeatingService.updateSeating(seatingTypeMapper.toEntity(seating), seatingId));
    }

    /**
     * Delete an Seating entity.
     * Deletes the Seating resource identified by the given UUID.
     *
     * @param seatingId the UUID of the Seating to delete
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteSeatingType(@Valid @RequestBody final UUID seatingId) {
        SeatingService.deleteSeating(seatingId);
    }

}
