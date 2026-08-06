import type { FileAttachmentUploadResponse } from "@/api/raumreservierung-backend";

export const mapResponseToFileMock = (
  response: FileAttachmentUploadResponse | undefined
) =>
  response && response.fileName
    ? new File([new Uint8Array(response.fileSize ?? 0)], response.fileName, {
        type: response.contentType,
      })
    : undefined;
