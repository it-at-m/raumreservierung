package de.muenchen.raumreservierung.booking.events;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class FutureBookingCheckEvent {
    private final UUID seatingTypeId;
    @Setter
    private boolean futureBookingExists;
}
