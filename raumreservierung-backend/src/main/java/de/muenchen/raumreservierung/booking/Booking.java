package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.booking.types.appointment.Appointment;
import de.muenchen.raumreservierung.booking.types.status.BookingStatus;
import de.muenchen.raumreservierung.common.BaseEntity;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.room.Room;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.io.Serial;
import java.time.LocalDateTime;
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
    private LocalDateTime start;

    @Column(nullable = false)
    private LocalDateTime end;

    @Column(nullable = false)
    private BookingStatus bookingStatus;

    @Column(nullable = false)
    private String title;

    //test for validity >0 & <1000
    // if room: <= max capacity of seatingtypecapacity of room
    //          or if no seatingtype selected: <= max capacity of room
    // if no room: <= max capacity of all rooms => roomController?
    @Column
    private int participantCount;

    @ManyToMany
    private Set<Equipment> equipments = new HashSet<>();

    @Column(length = 500)
    private String specialSeatingRequest;

    @Column
    private boolean cateringNeeded;

    @Column(length = 500)
    private String cateringCoordination;

    @Column(length = 2000)
    private String internalNotes;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "booking")
    private Set<Appointment> appointments = new HashSet<>();

    public void updateFrom(final Booking booking) {
        this.start = booking.getStart();
        this.end = booking.getEnd();
        this.room = booking.getRoom();
        this.bookingStatus = booking.getBookingStatus();
        this.title = booking.getTitle();
        this.participantCount = booking.getParticipantCount();
        this.equipments = booking.getEquipments();
        this.specialSeatingRequest = booking.getSpecialSeatingRequest();
        this.cateringNeeded = booking.isCateringNeeded();
        this.cateringCoordination = booking.getCateringCoordination();
        this.internalNotes = booking.getInternalNotes();
        this.appointments = booking.getAppointments();

        this.equipments.clear();
        if (booking.getEquipments() != null) {
            this.equipments.addAll(booking.getEquipments());
        }

        this.appointments.clear();
        if (booking.getAppointments() != null) {
            this.appointments.addAll(booking.getAppointments());
            this.appointments.forEach(csc -> csc.setBooking(this));
        }
    }
}
