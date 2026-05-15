import { useMemoize } from "@vueuse/core";

import { useGetRoom } from "@/composables/api/useRoomsApi.ts";

const { call, loading, error, data } = useGetRoom();

const getRoom = useMemoize(
  async (roomId: string) => {
    await call({ roomId });
    return data.value;
  },
  {
    getKey: (roomId) => roomId,
  }
);

export function useRoomCache() {
  return {
    call: getRoom,
    loading,
    error,
  };
}
