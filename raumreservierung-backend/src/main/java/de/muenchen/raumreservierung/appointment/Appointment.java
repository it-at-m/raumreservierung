package de.muenchen.raumreservierung.appointment;

import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import de.muenchen.raumreservierung.common.BaseEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import java.io.Serial;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
public class Appointment extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Embedded
    private ScheduleTemplate schedule;

    @ManyToOne
    private Booking booking;

    /**
     * Updates only the schedule field.
     * Appointments have a fixed relation to a booking.
     */
    public void updateFrom(final Appointment appointment) {
        this.schedule = appointment.getSchedule();
    }
}
