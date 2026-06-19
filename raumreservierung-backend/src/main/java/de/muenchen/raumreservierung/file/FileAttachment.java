package de.muenchen.raumreservierung.file;

import de.muenchen.raumreservierung.common.BaseEntity;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Entity
public class FileAttachment extends BaseEntity {

    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] data;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private long fileSize;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private boolean isAttached;

    public void updateFrom(MultipartFile multipartFile) throws IOException {
        this.data = multipartFile.getBytes();
        this.fileName = multipartFile.getOriginalFilename();
        this.contentType = multipartFile.getContentType();
        this.fileSize = multipartFile.getSize();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileAttachment)) {
            return false;
        }

        FileAttachment that = (FileAttachment) o;

        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
