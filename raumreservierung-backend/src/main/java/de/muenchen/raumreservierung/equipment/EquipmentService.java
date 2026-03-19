package de.muenchen.raumreservierung.equipment;

import de.muenchen.raumreservierung.common.ConflictException;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.security.Authorities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_CANNOT_DELETE_ACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public List<Equipment> findAll() {
        final List<Equipment> allEquipments = equipmentRepository.findAll();
        log.debug("Found {} equipments", allEquipments.size());
        return allEquipments;
    }

    @PreAuthorize(Authorities.EQUIPMENT_MANAGE)
    public Equipment createEquipment(final Equipment equipment) {
        log.debug("Creating equipment {}", equipment);
        return equipmentRepository.save(equipment);
    }

    @PreAuthorize(Authorities.EQUIPMENT_MANAGE)
    public Equipment updateEquipment(final Equipment equipment, final UUID equipmentId) {
        final Equipment foundEquipment = getEntityOrThrowException(equipmentId);
        foundEquipment.updateFrom(equipment);
        log.debug("Updating equipment to {}", foundEquipment);
        return equipmentRepository.save(foundEquipment);
    }

    @PreAuthorize(Authorities.EQUIPMENT_MANAGE)
    public void deleteEquipment(final UUID equipmentId) {
        final Equipment toDelete = getEntityOrThrowException(equipmentId);
        
        if (toDelete.isActive()) {
            throw new ConflictException(String.format(MSG_CANNOT_DELETE_ACTIVE, equipmentId));
        }

        log.debug("Deleting equipment {}", equipmentId);
        equipmentRepository.deleteById(equipmentId);
    }


    private Equipment getEntityOrThrowException(final UUID equipmentId) {
        return equipmentRepository
                .findById(equipmentId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, equipmentId)));
    }
}
