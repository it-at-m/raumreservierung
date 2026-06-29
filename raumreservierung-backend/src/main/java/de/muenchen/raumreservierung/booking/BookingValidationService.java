package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_STATUS_CHANGE_NOT_POSSIBLE;

import de.muenchen.raumreservierung.common.BadRequestException;
import de.muenchen.raumreservierung.person.PersonService;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.security.Roles;
import de.muenchen.raumreservierung.security.SecurityContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingValidationService {
    private final BookingTransitionService bookingTransitionService;
    private final SecurityContextService securityContextService;
    private final PersonService personService;

    public boolean canCancelBooking(final Booking booking) {
        return validateBookingAuthority(booking, Roles.RAUM_BUCHUNG) &&
                bookingTransitionService.isTransitionAllowed(booking.getStatus(), BookingStatus.CANCELLED);
    }

    public boolean isObligedToAutomaticStatusChange(final Booking booking) {
        return (booking.getStatus() == BookingStatus.COORDINATION_NEEDED
                || booking.getStatus() == BookingStatus.ROOM_APPROVED
                || booking.getStatus() == BookingStatus.ORGANIZER_APPROVED)
                && !securityContextService.hasAuthority(Roles.TERMIN_ORGANISATOR);
    }

    /**
     * Validates if the current user has the authority to access or modify a booking.
     *
     * @param booking The booking entity to validate access against.
     * @param role The specific security role that grants overriding access.
     * @return true if the user is authorized; false otherwise.
     */
    public boolean validateBookingAuthority(final Booking booking, final String role) {
        return securityContextService.hasAuthority(role) || isOwner(booking);
    }

    /**
     * Validates the status transition for a booking update.
     *
     * @param existingBooking the current booking state
     * @param bookingUpdates the requested booking updates
     * @throws BadRequestException if the status transition or cancellation is invalid
     */
    public void validateBookingStatusTransition(final Booking existingBooking, final Booking bookingUpdates) {
        final boolean isTransitionAllowed = bookingTransitionService.isTransitionAllowed(existingBooking.getStatus(), bookingUpdates.getStatus());
        final boolean isIllegalCancel = bookingUpdates.getStatus() == BookingStatus.CANCELLED && !canCancelBooking(existingBooking);

        if (!isTransitionAllowed || isIllegalCancel) {
            throw new BadRequestException(MSG_STATUS_CHANGE_NOT_POSSIBLE);
        }
    }

    private boolean isOwner(final Booking booking) {
        final InternalPerson internalPerson = personService.resolveInternalPersonByOrganisationIDOrThrowException(securityContextService.getCurrentOID());

        return booking.getBookedBy().getId().equals(internalPerson.getId()) || booking.getBookedFor().getId().equals(internalPerson.getId());
    }
}
