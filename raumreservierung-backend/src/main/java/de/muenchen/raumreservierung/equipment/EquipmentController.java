package de.muenchen.raumreservierung.equipment;

import de.muenchen.raumreservierung.equipment.dto.EquipmentMapper;
import de.muenchen.raumreservierung.equipment.dto.EquipmentRequestDto;
import de.muenchen.raumreservierung.equipment.dto.EquipmentResponseDto;
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
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    private final EquipmentMapper equipmentMapper;

    /**
     * Retrieve all equipment entities.
     * Returns a list of all existing equipment resources.
     *
     * @return list of equipment as response DTOs
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EquipmentResponseDto> getAllEquipments() {
        return equipmentService.findAll().stream().map(equipmentMapper::toDto).toList();
    }

    /**
     * Create a new equipment entity.
     * Creates a new equipment resource using the provided details.
     *
     * @param equipment the details of the equipment to create
     * @return the created equipment as response DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipmentResponseDto createEquipment(@Valid @RequestBody final EquipmentRequestDto equipment) {
        return equipmentMapper.toDto(equipmentService.createEquipment(equipmentMapper.toEntity(equipment)));
    }

    /**
     * Update an existing equipment entity.
     * Updates an existing equipment resource using the provided details.
     *
     * @param equipment the updated equipment details
     * @return the updated equipment as response DTO
     */
    @PutMapping("/{equipmentId}")
    @ResponseStatus(HttpStatus.OK)
    public EquipmentResponseDto updateEquipment(@Valid @RequestBody final EquipmentRequestDto equipment, @PathVariable("equipmentId") final UUID equipmentId) {
        return equipmentMapper.toDto(equipmentService.updateEquipment(equipmentMapper.toEntity(equipment), equipmentId));
    }

    /**
     * Delete an equipment entity.
     * Deletes the equipment resource identified by the given UUID.
     *
     * @param equipmentId the UUID of the equipment to delete
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteEquipment(@Valid @RequestBody final UUID equipmentId) {
        equipmentService.deleteEquipment(equipmentId);
    }


}
