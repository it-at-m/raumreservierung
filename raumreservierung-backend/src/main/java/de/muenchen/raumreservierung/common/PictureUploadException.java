package de.muenchen.raumreservierung.common;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Exception if data cannot be found. */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class PictureUploadException extends ResponseStatusException {
    /**
     * NotFoundException constructor
     *
     * @param message Exception message
     * @param e
     */
    public PictureUploadException(final String message, final IOException e) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
    }
}
