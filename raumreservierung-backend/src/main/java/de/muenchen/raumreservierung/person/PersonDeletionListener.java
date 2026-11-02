package de.muenchen.raumreservierung.person;

import java.util.UUID;

/**
 * Port for reacting to the deletion of a person.
 * Implemented by contexts that hold references to persons.
 */
public interface PersonDeletionListener {

    /**
     * Called synchronously within the transaction of the person deletion.
     *
     * @param personId the id of the deleted person.
     */
    void onPersonDeleted(UUID personId);
}
