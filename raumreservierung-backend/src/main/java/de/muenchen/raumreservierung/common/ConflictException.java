package de.muenchen.raumreservierung.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@SuppressWarnings("PMD.MissingSerialVersionUID")
public class ConflictException extends ResponseStatusException {
    public ConflictException(final String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
