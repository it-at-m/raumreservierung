import type {
  RoomDetailsResponseDTO,
  RoomRequestDTO,
  SeatingCapacityRequestDTO,
} from "@/api/raumreservierung-backend";

export const mapResponseToRequest = (
  response: RoomDetailsResponseDTO
): RoomRequestDTO => {
  const mappedEquipmentIds: string[] = [];
  if (response.equipment) {
    response.equipment.forEach((equip) => {
      if (equip.id) {
        mappedEquipmentIds.push(equip.id);
      }
    });
  }

  const mappedSeatingCapacities: SeatingCapacityRequestDTO[] = [];
  if (response.roomSeatingCapacities) {
    response.roomSeatingCapacities.forEach((seat) => {
      if (seat.seatingType.id) {
        mappedSeatingCapacities.push({
          seatingTypeId: seat.seatingType.id,
          capacity: seat.capacity,
        });
      }
    });
  }

  return {
    name: response.name,
    number: response.number,
    location: response.location,
    locationDescription: response.locationDescription,
    capacity: response.capacity,
    isActive: response.isActive,
    area: response.area,
    equipmentIds: mappedEquipmentIds,
    contactPersonId: response.contactPerson?.id,
    roomSeatingCapacities: mappedSeatingCapacities,
  };
};

export const EMPTY_ROOM_DATA = {
  name: "",
  number: "",
  location: "",
  locationDescription: "",
  capacity: 1,
  isActive: true,
  area: 1,
  roomSeatingCapacities: [],
  equipmentIds: [],
  contactPersonId: undefined,
};
