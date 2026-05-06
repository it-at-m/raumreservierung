package de.muenchen.raumreservierung.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Exception if data cannot be found. */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class UnauthorizedActionException extends ResponseStatusException {
    /**
     * NotFoundException constructor
     *
     * @param message Exception message
     */
    public UnauthorizedActionException(final String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
