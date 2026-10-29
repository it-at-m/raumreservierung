import type { UploadFileRequest } from "@/api/raumreservierung-backend";
import type { MaybeRefOrGetter } from "vue";

import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import { computed, toValue } from "vue";

import { FileAttachmentControllerApi } from "@/api/raumreservierung-backend";
import { ApiFactory } from "@/util/apiFactory.ts";
import { CACHE_KEYS } from "@/types/CACHE_KEYS.ts";

export const useUploadFile = () => {
  const api = ApiFactory.getInstance(FileAttachmentControllerApi);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: UploadFileRequest) => api.uploadFile(params),
    onSuccess: (data) => {
      queryClient.invalidateQueries({
        queryKey: [CACHE_KEYS.FILE_ATTACHMENT, data.id],
      });
    },
  });
};

export const useGetFile = (
  fileAttachmentId: MaybeRefOrGetter<string | undefined>
) => {
  const api = ApiFactory.getInstance(FileAttachmentControllerApi);
  const idRef = computed(() => toValue(fileAttachmentId));

  return useQuery({
    queryKey: [CACHE_KEYS.FILE_ATTACHMENT, idRef],
    queryFn: async () => {
      if (!idRef.value) {
        throw new Error("Room ID is required");
      }

      const apiResponse = await api.getFileRaw({
        fileId: idRef.value,
      });
      return await apiResponse.raw.blob();
    },
    enabled: computed(() => !!idRef.value),
  });
};
