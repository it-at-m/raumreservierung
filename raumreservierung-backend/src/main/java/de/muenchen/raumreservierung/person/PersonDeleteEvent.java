package de.muenchen.raumreservierung.person;

import java.util.UUID;

public record PersonDeleteEvent(UUID personId) {
}
