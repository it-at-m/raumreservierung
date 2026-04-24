package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.booking.types.BookingServiceTime;
import de.muenchen.raumreservierung.booking.types.BookingStatus;
import de.muenchen.raumreservierung.common.BaseEntity;
import de.muenchen.raumreservierung.equipment.Equipment;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Booking extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

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
    private List<Equipment> equipmentList;

    //merge of room backend branch needed first
    // seatingTypes and seatingTypesCapacity from selected room
    // how to handle special seating request
    //    @ManyToOne
    //    Room room;

    //    @ManyToOne
    //    private SeatingType seatingType;
    //    or
    //    @ManyToOne
    //    private SeatingTypeCapacity seatingTypeCapacity;

    @Column(length = 500)
    private String specialSeatingRequest;

    @Column
    private boolean cateringNeeded;

    @Column(length = 500)
    private String cateringCoordination;

    @Column(length = 2000)
    private String internalNotes;

    // new type ServiceTime needed for that
    @ElementCollection
    private List<BookingServiceTime> serviceTimes = new ArrayList<>();

    public void updateFrom(final Booking booking) {
        this.bookingStatus = booking.getBookingStatus();
        this.title = booking.getTitle();
        this.participantCount = booking.getParticipantCount();
        this.cateringNeeded = booking.isCateringNeeded();
        this.cateringCoordination = booking.getCateringCoordination();
        this.internalNotes = booking.getInternalNotes();
        this.serviceTimes = booking.getServiceTimes();
        this.equipmentList = booking.getEquipmentList();
    }
}
