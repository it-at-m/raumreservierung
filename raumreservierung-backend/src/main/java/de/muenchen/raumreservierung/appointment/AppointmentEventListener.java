package de.muenchen.raumreservierung.appointment;

import de.muenchen.raumreservierung.appointment.events.FutureAppointmentCheckEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentEventListener {

    private final AppointmentService appointmentService;

    @EventListener
    public void onFutureAppointmentCheck(final FutureAppointmentCheckEvent event) {
        event.setFutureAppointmentExists(appointmentService.existsFutureAppointmentForSeatingType(event.getSeatingTypeId()));
    }
}
