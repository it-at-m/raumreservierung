package de.muenchen.raumreservierung.equipment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;


    public List<Equipment> findAll() {
        List<Equipment> allEquipments = equipmentRepository.findAll();
        log.debug("Found {} equipments", allEquipments.size());
        return allEquipments;
    }

    public Equipment createEquipment(final Equipment equipment) {
        log.debug("Creating equipment {}", equipment);
        return equipmentRepository.save(equipment);
    }

    public Equipment updateEquipment(final Equipment equipment) {
        log.debug("Updating equipment {}", equipment);
        return equipmentRepository.save(equipment);
    }

    public void deleteEquipment(final UUID equipmentId) {
        log.debug("Deleting equipment {}", equipmentId);
        equipmentRepository.deleteById(equipmentId);
    }

}
