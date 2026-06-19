import type {
  FileAttachmentUploadResponse,
  GetFileRequest,
  UploadFileRequest,
} from "@/api/raumreservierung-backend";

import { FileAttachmentControllerApi } from "@/api/raumreservierung-backend";
import { useApi } from "@/composables/api/useApi.ts";
import { ApiFactory } from "@/util/apiFactory.ts";

export const useUploadFile = () => {
  const api = ApiFactory.getInstance(FileAttachmentControllerApi);

  return useApi<UploadFileRequest, FileAttachmentUploadResponse>((params) =>
    api.uploadFile(params)
  );
};

export const useGetFile = () => {
  const api = ApiFactory.getInstance(FileAttachmentControllerApi);

  return useApi<GetFileRequest, Blob>(async (params) => {
    const apiResponse = await api.getFileRaw(params);

    return await apiResponse.raw.blob();
  });
};
