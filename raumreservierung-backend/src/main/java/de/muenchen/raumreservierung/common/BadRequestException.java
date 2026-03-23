package de.muenchen.raumreservierung.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@SuppressWarnings("PMD.MissingSerialVersionUID")
public class BadRequestException extends ResponseStatusException {
    public BadRequestException(final String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
