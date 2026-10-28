package de.muenchen.raumreservierung.appointment.events;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class FutureAppointmentCheckEvent {
    private final UUID seatingTypeId;
    private final UUID roomId;
    @Setter
    private boolean futureAppointmentExists;
}
