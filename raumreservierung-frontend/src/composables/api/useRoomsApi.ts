import type {
  CreateRoomRequest,
  DeleteRoomRequest,
  GetAllRoomsRequest,
  UpdateRoomRequest,
} from "@/api/raumreservierung-backend";
import type { Ref } from "vue";

import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import { computed } from "vue";

import { RoomControllerApi } from "@/api/raumreservierung-backend";
import { ApiFactory } from "@/util/apiFactory.ts";

const ROOM_KEY = "room";

export const useGetAllRooms = (params: GetAllRoomsRequest) => {
  const api = ApiFactory.getInstance(RoomControllerApi);

  return useQuery({
    queryKey: [ROOM_KEY, "list"],
    queryFn: () => api.getAllRooms(params),
  });
};

export const useGetRoom = (roomId: Ref<string | undefined>) => {
  const api = ApiFactory.getInstance(RoomControllerApi);

  return useQuery({
    queryKey: [ROOM_KEY, roomId],
    queryFn: () => {
      if (!roomId.value) {
        throw new Error("Room ID is required");
      }

      return api.getRoom({ roomId: roomId.value });
    },
    enabled: computed(() => !!roomId.value),
  });
};

export const useDeleteRoom = () => {
  const api = ApiFactory.getInstance(RoomControllerApi);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: DeleteRoomRequest) => api.deleteRoom(params),
    onSuccess: (_data, variables) => {
      queryClient.removeQueries({ queryKey: [ROOM_KEY, variables.roomId] });
      queryClient.invalidateQueries({
        queryKey: [ROOM_KEY, "list"],
        exact: true,
      });
    },
  });
};

export const useUpdateRoom = () => {
  const api = ApiFactory.getInstance(RoomControllerApi);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: UpdateRoomRequest) => api.updateRoom(params),
    onSuccess: (data, variables) => {
      queryClient.setQueryData([ROOM_KEY, variables.roomId], data);
      queryClient.invalidateQueries({
        queryKey: [ROOM_KEY, "list"],
        exact: true,
      });
    },
  });
};

export const useCreateRoom = () => {
  const api = ApiFactory.getInstance(RoomControllerApi);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: CreateRoomRequest) => api.createRoom(params),
    onSuccess: (data) => {
      queryClient.setQueryData([ROOM_KEY, data.id], data);
      queryClient.invalidateQueries({
        queryKey: [ROOM_KEY, "list"],
        exact: true,
      });
    },
  });
};
