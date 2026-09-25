package de.muenchen.raumreservierung.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(
    {
            "Titel", "Datum_Buchungsbeginn", "Datum_Buchungsende", "Uhrzeit_Buchungsbeginn", "Uhrzeit_Buchungsende",
            "Teilnehmeranzahl", "Buchungstyp", "Status", "Raumname", "Ausstattung", "Bestuhlung", "Catering",
            "Gebucht von", "Gebucht von_Orgakürzel", "Firma_Extern", "Gebucht für", "Gebucht für_Orgakürzel",
            "Terminserie", "Veranstaltungszeitpunkt_Abweichend", "Datum_Veranstaltungsbeginn", "Datum_Veranstaltungsende",
            "Uhrzeit_Veranstaltungsbeginn", "Uhrzeit_Veranstaltungsende", "Notizen", "Interne Notizen"
    }
)
public record BookingExportDto(
        @JsonProperty("Titel") String title,
        @JsonProperty("Datum_Buchungsbeginn") String bookingStartDate,
        @JsonProperty("Datum_Buchungsende") String bookingEndDate,
        @JsonProperty("Uhrzeit_Buchungsbeginn") String bookingStartTime,
        @JsonProperty("Uhrzeit_Buchungsende") String bookingEndTime,
        @JsonProperty("Teilnehmeranzahl") Integer participantCount,
        @JsonProperty("Buchungstyp") String bookingType,
        @JsonProperty("Status") String status,
        @JsonProperty("Raumname") String roomName,
        @JsonProperty("Ausstattung") String equipment,
        @JsonProperty("Bestuhlung") String seatingType,
        @JsonProperty("Catering") String cateringNeeded,
        @JsonProperty("Gebucht von") String bookedBy,
        @JsonProperty("Gebucht von_Orgakürzel") String bookedByOrga,
        @JsonProperty("Firma_Extern") String externalCompany,
        @JsonProperty("Gebucht für") String bookedFor,
        @JsonProperty("Gebucht für_Orgakürzel") String bookedForOrga,
        @JsonProperty("Terminserie") String recurringRule,
        @JsonProperty("Veranstaltungszeitpunkt_Abweichend") String scheduleDeviates,
        @JsonProperty("Datum_Veranstaltungsbeginn") String appointmentStartDate,
        @JsonProperty("Datum_Veranstaltungsende") String appointmentEndDate,
        @JsonProperty("Uhrzeit_Veranstaltungsbeginn") String appointmentStartTime,
        @JsonProperty("Uhrzeit_Veranstaltungsende") String appointmentEndTime,
        @JsonProperty("Notizen") String additionalNotes,
        @JsonProperty("Interne Notizen") String internalNotes) {
}
