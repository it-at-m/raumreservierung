import type {
  CreateRoomRequest,
  DeleteRoomRequest,
  GetRoomRequest,
  RoomDetailsResponseDTO,
  RoomListResponseDTO,
  UpdateRoomRequest,
} from "@/api/raumreservierung-backend";

import { RoomControllerApi } from "@/api/raumreservierung-backend";
import { useApi } from "@/composables/api/useApi.ts";
import { ApiFactory } from "@/util/apiFactory.ts";

export const useGetAllRooms = () => {
  const api = ApiFactory.getInstance(RoomControllerApi);
  // eslint-disable-next-line @typescript-eslint/no-invalid-void-type
  return useApi<void, RoomListResponseDTO[]>(() => api.getAllRooms());
};

export const useGetRoom = () => {
  const api = ApiFactory.getInstance(RoomControllerApi);

  return useApi<GetRoomRequest, RoomDetailsResponseDTO>((params) =>
    api.getRoom(params)
  );
};

export const useDeleteRoom = () => {
  const api = ApiFactory.getInstance(RoomControllerApi);

  // eslint-disable-next-line @typescript-eslint/no-invalid-void-type
  return useApi<DeleteRoomRequest, void>((params) => api.deleteRoom(params));
};

export const useUpdateRoom = () => {
  const api = ApiFactory.getInstance(RoomControllerApi);

  return useApi<UpdateRoomRequest, RoomDetailsResponseDTO>((params) =>
    api.updateRoom(params)
  );
};

export const useCreateRoom = () => {
  const api = ApiFactory.getInstance(RoomControllerApi);

  return useApi<CreateRoomRequest, RoomDetailsResponseDTO>((params) =>
    api.createRoom(params)
  );
};
