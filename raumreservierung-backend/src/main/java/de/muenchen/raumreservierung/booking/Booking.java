package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.appointment.Appointment;
import de.muenchen.raumreservierung.common.BaseEntity;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.person.Person;
import de.muenchen.raumreservierung.room.Room;
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

    //test for validity >0 & <1000
    // if room: <= max capacity of seatingtypecapacity of room
    //          or if no seatingtype selected: <= max capacity of room
    // if no room: <= max capacity of all rooms => roomController?
    @Column
    private int participantCount;

    @ManyToMany
    private Set<Equipment> equipment = new HashSet<>();

    @Column
    private boolean cateringNeeded;

    @Column(length = 500)
    private String internalNotes;

    @Column(length = 500)
    private String additionalNotes;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "booking")
    private Set<Appointment> appointments = new HashSet<>();

    @Embedded
    private ScheduleTemplate schedule;

    @ManyToOne
    private Person contactPerson;

    public void updateFrom(final Booking booking) {
        this.room = booking.getRoom();
        this.title = booking.getTitle();
        this.participantCount = booking.getParticipantCount();
        this.equipment = booking.getEquipment();
        this.cateringNeeded = booking.isCateringNeeded();
        this.internalNotes = booking.getInternalNotes();
        this.additionalNotes = booking.getAdditionalNotes();
        this.appointments = booking.getAppointments();
        this.schedule = booking.getSchedule();
        this.contactPerson = booking.getContactPerson();

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
