import type {
  ExternalPersonResponseDto,
  InternalPersonResponseDto,
  UpdatePersonRequest,
} from "@/api/raumreservierung-backend/models";

// Pfad anpassen

type PersonResponseDto = ExternalPersonResponseDto | InternalPersonResponseDto;

/**
 * Wandelt ein Response-DTO (aus GET/PUT-Antworten) in das passende
 * Request-DTO um, wie es die update/create-Endpoints erwarten.
 *
 * Wirft, wenn Pflichtfelder (firstName/lastName) fehlen.
 */
export function mapPersonResponseToRequest(
  person: PersonResponseDto,
  type: "EXTERNAL" | "INTERNAL"
): UpdatePersonRequest {
  if (type === "EXTERNAL") {
    const externalPerson = person as ExternalPersonResponseDto;
    return {
      type: "EXTERNAL",
      title: person.title,
      firstName: person.firstName,
      lastName: person.lastName,
      email: person.email,
      telefonNumber: person.telefonNumber,
      company: externalPerson.company,
      streetAddress: externalPerson.streetAddress,
      postalCodeCity: externalPerson.postalCodeCity,
      note: externalPerson.note,
    };
  }
  if (!person.firstName || !person.lastName) {
    throw new Error("firstName and lastName are required");
  }

  const internalPerson = person as InternalPersonResponseDto;
  return {
    type: "INTERNAL",
    title: person.title,
    firstName: person.firstName,
    lastName: person.lastName,
    email: person.email,
    telefonNumber: person.telefonNumber,
    organisationId: internalPerson.organisationId,
    organisationUnit: internalPerson.organisationUnit,
  };
}
