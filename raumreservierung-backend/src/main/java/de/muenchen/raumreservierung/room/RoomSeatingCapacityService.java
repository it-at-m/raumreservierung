package de.muenchen.raumreservierung.room;

import de.muenchen.raumreservierung.seating.SeatingService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomSeatingCapacityService {

    private final SeatingService seatingService;

    public List<RoomSeatingCapacity> fillSeatingCapacities(final List<RoomSeatingCapacity> roomCapacities, final Room room) {
        final List<RoomSeatingCapacity> capacities = new ArrayList<>(
                Optional.ofNullable(roomCapacities)
                        .orElse(Collections.emptyList()));

        capacities
                .forEach(seatingCapacity -> {
                    seatingCapacity.setSeatingType(
                            seatingService.getReferenceById(
                                    seatingCapacity.getSeatingType().getId()));
                    seatingCapacity.setRoom(room);
                });
        return capacities;
    }
}
