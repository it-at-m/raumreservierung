package de.muenchen.raumreservierung.booking.events;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RemoveRoomFromBookingsEvent {
    private final UUID roomId;
}
