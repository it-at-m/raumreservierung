package de.muenchen.raumreservierung.file;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileAttachmentRepository extends JpaRepository<FileAttachment, UUID> {
    List<FileAttachment> findByCreatedAtBefore(OffsetDateTime createdAtBefore);

    void deleteByIsAttachedFalseAndCreatedAtBefore(OffsetDateTime threshold);
}
