package de.muenchen.raumreservierung.export;

import de.muenchen.raumreservierung.appointment.Appointment;
import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.person.domain.ExternalPerson;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.person.domain.Person;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface ExportMapper {
    @Mapping(target = "title", source = "booking.title")
    @Mapping(target = "participantCount", source = "booking.participantCount")
    @Mapping(target = "bookingType", source = "booking.bookingType")
    @Mapping(target = "status", source = "booking.status")
    @Mapping(target = "roomName", source = "booking.room.name")

    @Mapping(target = "equipment", source = "booking.equipment", qualifiedByName = "mapEquipment")
    @Mapping(target = "seatingType", source = "booking.seatingType.name")
    @Mapping(target = "cateringNeeded", source = "booking.cateringNeeded", qualifiedByName = "booleanToReadableString")

    @Mapping(target = "bookedBy", source = "booking.bookedBy", qualifiedByName = "mapPersonName")
    @Mapping(target = "bookedByOrga", source = "booking.organisationUnit")
    @Mapping(target = "externalCompany", source = "booking.bookedFor", qualifiedByName = "mapExternalPersonToCompany")
    @Mapping(target = "bookedFor", source = "booking.bookedFor", qualifiedByName = "mapPersonName")
    @Mapping(target = "bookedForOrga", source = "booking.bookedFor", qualifiedByName = "mapPersonToInternalOrganization")

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
     * Mapper methods below are for adjusted for human-readable format inside a csv file and not
     * optimized for further processing
     */
    @Named("booleanToReadableString")
    default String booleanToReadableString(final boolean value) {
        return value ? "Ja" : "Nein";
    }

    @Named("mapEquipment")
    default String mapEquipment(final Set<Equipment> equipmentSet) {
        if (equipmentSet == null || equipmentSet.isEmpty()) {
            return null;
        }
        return equipmentSet.stream()
                .map(Equipment::getName)
                .collect(Collectors.joining(", "));
    }

    @Named("mapPersonName")
    default String mapPersonName(final Person person) {
        return (person == null) ? null : String.format("%s %s", person.getFirstName(), person.getLastName());
    }

    @Named("mapExternalPersonToCompany")
    default String mapExternalCompany(final Person person) {
        return (person instanceof ExternalPerson externalPerson) ? externalPerson.getCompany() : null;
    }

    @Named("mapPersonToInternalOrganization")
    default String mapInternalOrganization(final Person person) {
        return (person instanceof InternalPerson internalPerson) ? internalPerson.getOrganisationUnit() : null;
    }

    @Named("mapDate")
    default String mapDate(final OffsetDateTime dateTime) {
        return (dateTime == null) ? null : dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Named("mapTime")
    default String mapTime(final OffsetDateTime dateTime) {
        return (dateTime == null) ? null : dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Named("checkDeviation")
    default String checkDeviation(final ScheduleTemplate schedule) {
        return booleanToReadableString(schedule != null && schedule.appointmentStart() != null && schedule.appointmentEnd() != null
                && (!schedule.occupancyStart().equals(schedule.appointmentStart()) ||
                        !schedule.occupancyEnd().equals(schedule.appointmentEnd())));
    }
}
