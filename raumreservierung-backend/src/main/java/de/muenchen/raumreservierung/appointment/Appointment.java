package de.muenchen.raumreservierung.appointment;

import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import de.muenchen.raumreservierung.common.BaseEntity;
import jakarta.persistence.Column;
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

    @Column(nullable = false)
    private ScheduleTemplate schedule;

    @ManyToOne
    private Booking booking;

    public void updateFrom(final Appointment appointment) {
        this.booking = appointment.getBooking();
        this.schedule = appointment.getSchedule();
    }
}
