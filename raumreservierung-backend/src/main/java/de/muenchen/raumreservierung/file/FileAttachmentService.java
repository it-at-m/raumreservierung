package de.muenchen.raumreservierung.file;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_FILE_READING_ERROR;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.raumreservierung.common.NotFoundException;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Slf4j
@Service
public class FileAttachmentService {

    private final FileAttachmentRepository fileAttachmentRepository;

    @Transactional(readOnly = true)
    public FileAttachment findById(final UUID fileId) {
        log.info("Finding file attachment by id {}", fileId);
        return fileAttachmentRepository.findById(fileId).orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, fileId)));
    }

    @Transactional
    public FileAttachment createFile(final MultipartFile multipartFile) {
        try {
            final FileAttachment fileAttachment = new FileAttachment();
            fileAttachment.updateFrom(multipartFile);
            return fileAttachmentRepository.save(fileAttachment);
        } catch (final IOException e) {
            throw new RuntimeException(MSG_FILE_READING_ERROR, e);
        }
    }

    @Transactional
    public void deleteFile(final UUID fileId) {
        log.debug("Deleting file {}", fileId);
        fileAttachmentRepository.deleteById(fileId);
    }

    @Transactional
    public void attachFileAttachment(final UUID fileAttachmentId) {
        final FileAttachment fileAttachment = getEntityOrThrowException(fileAttachmentId);
        fileAttachment.setAttached(true);

        fileAttachmentRepository.save(fileAttachment);
    }

    @Transactional
    public void unAttachFileAttachment(final UUID fileAttachmentId) {
        final FileAttachment fileAttachment = getEntityOrThrowException(fileAttachmentId);
        fileAttachment.setAttached(false);

        fileAttachmentRepository.save(fileAttachment);
    }

    @Transactional
    public void cleanupOrphanedFiles(final OffsetDateTime threshold) {
        fileAttachmentRepository.deleteByIsAttachedFalseAndCreatedAtBefore(threshold);
    }

    private FileAttachment getEntityOrThrowException(final UUID fileAttachmentId) {
        return fileAttachmentRepository
                .findById(fileAttachmentId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, fileAttachmentId)));
    }

}
