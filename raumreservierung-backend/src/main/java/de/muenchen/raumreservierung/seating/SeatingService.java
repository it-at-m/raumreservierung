package de.muenchen.raumreservierung.seating;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_CANNOT_DELETE_ACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.raumreservierung.common.ConflictException;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.security.Authorities;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeatingService {

    private final SeatingRepository seatingRepository;

    public List<SeatingType> findAll() {
        final List<SeatingType> allSeatingTypes = seatingRepository.findAll();
        log.debug("Found {} Seatings", allSeatingTypes.size());
        return allSeatingTypes;
    }

    @PreAuthorize(Authorities.SEATING_MANAGE)
    public SeatingType createSeating(final SeatingType seatingType) {
        log.debug("Creating Seating {}", seatingType);
        return seatingRepository.save(seatingType);
    }

    @PreAuthorize(Authorities.SEATING_MANAGE)
    public SeatingType updateSeating(final SeatingType seatingType, final UUID seatingId) {
        final SeatingType foundSeatingType = getEntityOrThrowException(seatingId);
        foundSeatingType.updateFrom(seatingType);
        log.debug("Updating Seating to {}", foundSeatingType);
        return seatingRepository.save(foundSeatingType);
    }

    @PreAuthorize(Authorities.SEATING_MANAGE)
    public void deleteSeating(final UUID seatingTypeId) {
        final SeatingType toDelete = getEntityOrThrowException(seatingTypeId);

        if (toDelete.isActive()) {
            throw new ConflictException(String.format(MSG_CANNOT_DELETE_ACTIVE, seatingTypeId));
        }

        log.debug("Deleting Seating {}", seatingTypeId);
        seatingRepository.delete(toDelete);
    }

    private SeatingType getEntityOrThrowException(final UUID seatingTypeId) {
        return seatingRepository
                .findById(seatingTypeId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, seatingTypeId)));
    }
}
