package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_STATUS_CHANGE_NOT_POSSIBLE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_UPDATE_OF_FIELDS_NOT_ALLOWED;

import de.muenchen.raumreservierung.common.BadRequestException;
import de.muenchen.raumreservierung.security.Roles;
import de.muenchen.raumreservierung.security.SecurityContextService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Domain service holding the booking status machine and organizer authority rules.
 * Free of persistence concerns.
 */
@Service
@RequiredArgsConstructor
public class BookingDomainService {

    private final BookingTransitionService bookingTransitionService;
    private final SecurityContextService securityContextService;

    /**
     * Determines whether the booking status must change automatically.
     * Automatic changes only in bookings with state COORDINATION_NEEDED, ROOM_APPROVED or
     * ORGANIZER_APPROVED.
     * Users with role TERMIN_ORGANISATOR or higher are explicitly excepted from this automation.
     *
     * @param booking the booking entity to evaluate
     * @return true if the booking is in an eligible state and the user is not a coordinator or higher,
     *         false otherwise
     */
    public boolean isObligedToAutomaticStatusChange(final Booking booking) {
        return (booking.getStatus() == BookingStatus.COORDINATION_NEEDED
                || booking.getStatus() == BookingStatus.ROOM_APPROVED
                || booking.getStatus() == BookingStatus.ORGANIZER_APPROVED)
                && !securityContextService.hasAuthority(Roles.TERMIN_ORGANISATOR);
    }

    /**
     * Validates the status transition for a booking update.
     *
     * @param existingBooking the current booking state
     * @param bookingUpdates the requested booking updates
     * @throws BadRequestException if the status transition or cancellation is invalid
     */
    public void validateBookingStatusTransitionOrThrowException(final Booking existingBooking, final Booking bookingUpdates) {
        if (existingBooking != null) {
            final boolean isTransitionAllowed = bookingTransitionService.isTransitionAllowed(existingBooking.getStatus(), bookingUpdates.getStatus());

            if (!isTransitionAllowed) {
                throw new BadRequestException(MSG_STATUS_CHANGE_NOT_POSSIBLE);
            }
        }
    }

    /**
     * Validates if the update is restricted solely to a terminal booking status (and its reason if
     * status is unfeasible).
     *
     * @param bookingUpdates the requested booking updates
     * @param existingBooking the currently existing booking
     * @throws BadRequestException if other fields are modified
     */
    public void validateTerminalStatusOrThrowException(final Booking bookingUpdates, final Booking existingBooking) {
        if (existingBooking == null) {
            return;
        }
        final Booking expectedBooking = new Booking();
        expectedBooking.updateFrom(existingBooking);
        // status is the only field that can be changed by user
        expectedBooking.setStatus(bookingUpdates.getStatus());
        // these fields can't be changed by user
        expectedBooking.setId(bookingUpdates.getId());
        expectedBooking.setBookedBy(bookingUpdates.getBookedBy());
        expectedBooking.setOrganisationUnit(bookingUpdates.getOrganisationUnit());

        if (bookingUpdates.getStatus() == BookingStatus.UNFEASIBLE) {
            expectedBooking.setReasonForStatusChange(bookingUpdates.getReasonForStatusChange());
        }

        if (!bookingUpdates.equals(expectedBooking)) {
            throw new BadRequestException(MSG_UPDATE_OF_FIELDS_NOT_ALLOWED);
        }
    }

    /**
     * Checks whether the given status is a terminal status of the booking state machine.
     *
     * @param status the status to check
     * @return true if the status is terminal; false otherwise
     */
    public boolean isTerminalStatus(final BookingStatus status) {
        return status == BookingStatus.CANCELED || status == BookingStatus.UNFEASIBLE;
    }

    /**
     * Determines whether an automatic status change is required based on modifications
     * to the booking details.
     *
     * @param existingBooking the current booking before updates
     * @param bookingUpdate the updated booking information
     * @return true if any of the following have changed: the room, the appointments,
     *         or the service requirement; false otherwise
     */
    public boolean needForAutomaticStatusChange(final Booking existingBooking, final Booking bookingUpdate) {
        return !Objects.equals(existingBooking.getRoom(), bookingUpdate.getRoom()) // room changed
                || !Objects.equals(existingBooking.getEquipment(), bookingUpdate.getEquipment()) // service changed
                || !Objects.equals(existingBooking.getSeatingType(), bookingUpdate.getSeatingType())
                || existingBooking.getParticipantCount() != bookingUpdate.getParticipantCount()
                || existingBooking.isCateringNeeded() != bookingUpdate.isCateringNeeded()
                || !Objects.equals(existingBooking.getRecurringRule(), bookingUpdate.getRecurringRule()) // schedule changed
                || !Objects.equals(existingBooking.getSchedule(), bookingUpdate.getSchedule());
    }

    /**
     * Resets bookingType/internalNotes to their previous values if the caller lacks
     * Role TERMIN_ORGANISATOR, then sets the booking status accordingly.
     *
     * @param booking the booking to update (new or existing)
     * @param previousType the type to fall back to if unauthorized
     * @param previousNotes the notes to fall back to if unauthorized
     */
    public void applyOrganizerAuthorityRules(final Booking booking, final BookingType previousType, final String previousNotes) {
        if (!securityContextService.hasAuthority(Roles.TERMIN_ORGANISATOR)) {
            booking.setInternalNotes(previousNotes);
            booking.setBookingType(previousType);
        }

        if (booking.getBookingType() == BookingType.DEFAULT) {
            if (previousType != BookingType.DEFAULT) {
                // if type changed from other than DEFAULT to DEFAULT then set status to ROOM_CHANGED
                // to not allow circumventing the status-state-machine
                booking.setStatus(BookingStatus.ROOM_CHANGED);
            }
        } else {
            booking.setStatus(BookingStatus.ORGANIZER_APPROVED);
        }
    }

    /**
     * Automatically updates the booking status to {@link BookingStatus#ROOM_CHANGED} if the room,
     * appointment, or service has changed, provided the user does not hold the role
     * {@link Roles#TERMIN_ORGANISATOR} or higher.
     *
     * @param bookingUpdates the updated booking containing the requested changes
     * @param existingBooking the current booking
     */
    public void applyAutomaticStatusChangesIfApplicable(final Booking bookingUpdates, final Booking existingBooking) {
        if (isObligedToAutomaticStatusChange(existingBooking)
                && needForAutomaticStatusChange(existingBooking, bookingUpdates)) {
            bookingUpdates.setStatus(BookingStatus.ROOM_CHANGED);
        }
    }
}
