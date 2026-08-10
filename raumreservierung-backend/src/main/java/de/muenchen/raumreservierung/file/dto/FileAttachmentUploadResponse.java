package de.muenchen.raumreservierung.file.dto;

import java.util.UUID;

public record FileAttachmentUploadResponse(
        UUID id,
        String fileName,
        long fileSize,
        String contentType) {
}
