package de.muenchen.raumreservierung.file.dto;

import de.muenchen.raumreservierung.file.FileAttachment;
import org.mapstruct.Mapper;

@Mapper
public interface FileAttachmentMapper {

    FileAttachmentUploadResponse toDto(FileAttachment fileAttachment);
}
