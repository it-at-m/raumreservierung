package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.appointment.Appointment;
import de.muenchen.raumreservierung.common.BaseEntity;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.person.domain.Person;
import de.muenchen.raumreservierung.room.Room;
import de.muenchen.raumreservierung.seating.SeatingType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.io.Serial;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Entity
@Setter
@Getter
public class Booking extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Room room;

    @Column(nullable = false)
    private String title;

    @Column
    @Range(min = 0, max = 99_999)
    private int participantCount;

    @ManyToMany
    private Set<Equipment> equipment = new HashSet<>();

    @Column
    private boolean cateringNeeded;

    @Column(length = 500)
    private String internalNotes;

    @Column(length = 500)
    private String additionalNotes;

    @Column
    private String recurringRule;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "booking")
    private Set<Appointment> appointments = new HashSet<>();

    @Embedded
    private ScheduleTemplate schedule;

    @ManyToOne(optional = false)
    private Person bookedBy;

    @ManyToOne
    private Person bookedFor;

    @Column(nullable = false)
    private String organisationUnit;

    @ManyToOne
    private SeatingType seatingType;

    //TODO: add status

    public void updateFrom(final Booking booking) {
        this.room = booking.getRoom();
        this.title = booking.getTitle();
        this.participantCount = booking.getParticipantCount();
        this.cateringNeeded = booking.isCateringNeeded();
        this.internalNotes = booking.getInternalNotes();
        this.additionalNotes = booking.getAdditionalNotes();
        this.schedule = booking.getSchedule();
        this.bookedBy = booking.getBookedBy();
        this.bookedFor = booking.getBookedFor();
        this.recurringRule = booking.getRecurringRule();
        this.organisationUnit = booking.getOrganisationUnit();
        this.seatingType = booking.getSeatingType();

        this.equipment.clear();
        if (booking.getEquipment() != null) {
            this.equipment.addAll(booking.getEquipment());
        }

        this.appointments.clear();
        if (booking.getAppointments() != null) {
            this.appointments.addAll(booking.getAppointments());
            this.appointments.forEach(csc -> csc.setBooking(this));
        }
    }
}
