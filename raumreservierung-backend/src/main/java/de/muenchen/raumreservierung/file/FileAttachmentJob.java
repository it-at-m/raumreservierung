package de.muenchen.raumreservierung.file;

import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileAttachmentJob {

    private final FileAttachmentService fileAttachmentService;

    @Scheduled(cron = "${raumreservierung.scheduling.file-attachment-cleanup}")
    public void cleanupUnAttachedFiles() {
        log.info("Cleaning up un-attached files");
        final OffsetDateTime cleanupAfter = OffsetDateTime.now().minusHours(1);

        fileAttachmentService.cleanupOrphanedFiles(cleanupAfter);
    }

}
