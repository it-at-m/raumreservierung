package de.muenchen.raumreservierung.equipment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Equipment> getAllEquipments() {
        return equipmentService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Equipment createEquipment(@Valid @RequestBody final Equipment equipment) {
        return equipmentService.createEquipment(equipment);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Equipment updateEquipment(@Valid @RequestBody final Equipment equipment) {
        return equipmentService.updateEquipment(equipment);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteEquipment(@Valid @RequestBody final UUID equipmentId) {
        equipmentService.deleteEquipment(equipmentId);
    }


}
