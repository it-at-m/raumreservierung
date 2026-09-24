package de.muenchen.raumreservierung.export;

import de.muenchen.raumreservierung.appointment.Appointment;
import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.person.domain.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface ExportMapper {
    @Mapping(target = "title", source = "booking.title")
    @Mapping(target = "participantCount", source = "booking.participantCount")
    @Mapping(target = "bookingType", source = "booking.bookingType")
    @Mapping(target = "status", source = "booking.status")
    @Mapping(target = "roomName", source = "booking.room.name")

    @Mapping(target = "equipment", source = "booking.equipment", qualifiedByName = "mapEquipment")
    @Mapping(target = "seatingType", source = "booking.seatingType.name")
    @Mapping(target = "cateringNeeded", source = "booking.cateringNeeded", qualifiedByName = "booleanToReadableString")

    @Mapping(target = "bookedBy", source = "booking.bookedBy", qualifiedByName = "mapPerson")
    @Mapping(target = "bookedByOrga", source = "booking.organisationUnit")
    @Mapping(target = "externalCompany", constant = "") // TODO
    @Mapping(target = "bookedFor", source = "booking.bookedFor", qualifiedByName = "mapPerson")
    @Mapping(target = "bookedForOrga", constant = "") // TODO

    @Mapping(target = "recurringRule", source = "booking.recurringRule")
    @Mapping(target = "additionalNotes", source = "booking.additionalNotes")
    @Mapping(target = "internalNotes", source = "booking.internalNotes")

    @Mapping(target = "bookingStartDate", source = "schedule.occupancyStart", qualifiedByName = "mapDate")
    @Mapping(target = "bookingEndDate", source = "schedule.occupancyEnd", qualifiedByName = "mapDate")
    @Mapping(target = "bookingStartTime", source = "schedule.occupancyStart", qualifiedByName = "mapTime")
    @Mapping(target = "bookingEndTime", source = "schedule.occupancyEnd", qualifiedByName = "mapTime")

    @Mapping(target = "appointmentStartDate", source = "schedule.appointmentStart", qualifiedByName = "mapDate")
    @Mapping(target = "appointmentEndDate", source = "schedule.appointmentEnd", qualifiedByName = "mapDate")
    @Mapping(target = "appointmentStartTime", source = "schedule.appointmentStart", qualifiedByName = "mapTime")
    @Mapping(target = "appointmentEndTime", source = "schedule.appointmentEnd", qualifiedByName = "mapTime")

    @Mapping(target = "scheduleDeviates", source = "schedule", qualifiedByName = "checkDeviation")
    BookingExportDto toExportDto(Appointment appointment);

    /**
     * Mapper methods below are for adjusted for human-readable format inside a csv file and not optimized for further processing
     */
    // TODO determine if null or empty string is better for end result!
    @Named("booleanToReadableString")
    default String booleanToReadableString(boolean value) {
        return value ? "Ja" : "Nein";
    }

    @Named("mapEquipment")
    default String mapEquipment(Set<Equipment> equipmentSet) {
        if (equipmentSet == null || equipmentSet.isEmpty()) {
            return null;
        }
        return equipmentSet.stream()
                .map(Equipment::getName)
                .collect(Collectors.joining(", "));
    }

    @Named("mapPerson")
    default String mapPerson(Person person) {
        return (person == null) ? null : String.format("%s %s", person.getFirstName(), person.getLastName());
    }

    @Named("mapDate")
    default String mapDate(OffsetDateTime dateTime) {
        return (dateTime == null) ? null : dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Named("mapTime")
    default String mapTime(OffsetDateTime dateTime) {
        return (dateTime == null) ? null : dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Named("checkDeviation")
    default String checkDeviation(ScheduleTemplate schedule) {
        return booleanToReadableString(schedule != null && schedule.appointmentStart() != null && schedule.appointmentEnd() != null && (!schedule.occupancyStart().equals(schedule.appointmentStart()) ||
                !schedule.occupancyEnd().equals(schedule.appointmentEnd())));
    }
}
