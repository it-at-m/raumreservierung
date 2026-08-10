package de.muenchen.raumreservierung.file;

import de.muenchen.raumreservierung.common.BaseEntity;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import java.io.IOException;
import java.io.Serial;
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

    @Serial
    private static final long serialVersionUID = 1L;

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

    public void updateFrom(final MultipartFile multipartFile) throws IOException {
        this.data = multipartFile.getBytes();
        this.fileName = multipartFile.getOriginalFilename();
        this.contentType = multipartFile.getContentType();
        this.fileSize = multipartFile.getSize();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileAttachment that)) {
            return false;
        }

        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
