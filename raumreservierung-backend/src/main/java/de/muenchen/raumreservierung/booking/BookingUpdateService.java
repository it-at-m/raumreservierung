package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_UNAUTHORIZED_ACTION;

import de.muenchen.raumreservierung.appointment.Appointment;
import de.muenchen.raumreservierung.appointment.AppointmentService;
import de.muenchen.raumreservierung.common.UnauthorizedActionException;
import de.muenchen.raumreservierung.security.Roles;
import de.muenchen.raumreservierung.security.SecurityContextService;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingUpdateService {

    private final BookingValidationService bookingValidationService;
    private final SecurityContextService securityContextService;
    private final AppointmentService appointmentService;

    public void applyBookingUpdate(
            final Booking existingBooking,
            final Booking bookingUpdates) {

        switch (bookingUpdates.getStatus()) {
            case CANCELED -> applyCancelStatus(existingBooking, bookingUpdates);
            case UNFEASIBLE -> applyUnfeasibleStatus(existingBooking, bookingUpdates);
            default -> handleStandardBookingUpdate(existingBooking, bookingUpdates);
        }
    }

    private void applyCancelStatus(
            final Booking existingBooking,
            final Booking bookingUpdates) {

        existingBooking.setStatus(BookingStatus.CANCELED);
        bookingUpdates.updateFrom(existingBooking);
    }

    private void applyUnfeasibleStatus(
            final Booking existingBooking,
            final Booking bookingUpdates) {

        if (!securityContextService.hasAuthority(Roles.RAUM_BUCHUNG)) {
            throw new UnauthorizedActionException(MSG_UNAUTHORIZED_ACTION);
        }

        existingBooking.setStatus(BookingStatus.UNFEASIBLE);
        existingBooking.setReasonForStatusChange(bookingUpdates.getReasonForStatusChange());

        bookingUpdates.updateFrom(existingBooking);
    }

    private void handleStandardBookingUpdate(
            final Booking existingBooking,
            final Booking bookingUpdates) {
        validateAndAuthorizeUpdate(bookingUpdates, existingBooking);
        applyOrganizerAuthorityRules(bookingUpdates, existingBooking.getBookingType(), existingBooking.getInternalNotes());
        applyAutomaticStatusChangesIfApplicable(bookingUpdates, existingBooking);
        updateBookingAppointments(existingBooking, bookingUpdates);
    }

    /**
     * Validates the booking update and checks that the current user is authorized to perform it.
     *
     * @param bookingUpdates booking data with the requested changes
     * @param existingBooking existing booking to be updated
     * @throws RuntimeException if validation fails or the user lacks the required authority
     */
    private void validateAndAuthorizeUpdate(final Booking bookingUpdates, final Booking existingBooking) {
        bookingValidationService.bookingIsValidOrThrowException(bookingUpdates, existingBooking);
        bookingValidationService.checkAuthorityOrThrowException(existingBooking, Roles.TERMIN_ORGANISATOR);
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
                // if type changed from other than DEFAULT to DEFAULT then set status to ROOM_CHANGED to not allow circumventing the status-state-machine
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
    private void applyAutomaticStatusChangesIfApplicable(final Booking bookingUpdates, final Booking existingBooking) {
        if (bookingValidationService.isObligedToAutomaticStatusChange(existingBooking)
                && needForAutomaticStatusChange(existingBooking, bookingUpdates)) {
            bookingUpdates.setStatus(BookingStatus.ROOM_CHANGED);
        }
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
    private boolean needForAutomaticStatusChange(final Booking existingBooking, final Booking bookingUpdate) {
        return !Objects.equals(existingBooking.getRoom(), bookingUpdate.getRoom()) // room changed
                || !Objects.equals(existingBooking.getEquipment(), bookingUpdate.getEquipment()) // service changed
                || !Objects.equals(existingBooking.getSeatingType(), bookingUpdate.getSeatingType())
                || existingBooking.getParticipantCount() != bookingUpdate.getParticipantCount()
                || existingBooking.isCateringNeeded() != bookingUpdate.isCateringNeeded()
                || !Objects.equals(existingBooking.getRecurringRule(), bookingUpdate.getRecurringRule()) // schedule changed
                || !Objects.equals(existingBooking.getSchedule(), bookingUpdate.getSchedule());
    }

    /**
     * Updates the appointments of a booking when its recurring rule changes.
     * It preserves all past appointments and merges them with newly generated future appointments based
     * on the updated rule.
     *
     * @param existingBooking the current state of the booking
     * @param bookingUpdates the updated booking data
     */
    public void updateBookingAppointments(final Booking existingBooking, final Booking bookingUpdates) {
        if (Objects.equals(existingBooking.getRecurringRule(), bookingUpdates.getRecurringRule())
                && Objects.equals(existingBooking.getSchedule(), bookingUpdates.getSchedule())) {
            bookingUpdates.setAppointments(new HashSet<>(existingBooking.getAppointments()));
            return;
        }

        final Set<Appointment> newAppointments = appointmentService.generateAndLinkAppointments(bookingUpdates);
        if (bookingUpdates.getRecurringRule() == null || bookingUpdates.getRecurringRule().isBlank()) {
            bookingUpdates.setAppointments(newAppointments);
            return;
        }

        final OffsetDateTime now = OffsetDateTime.now();

        final Set<Appointment> pastAppointments = existingBooking.getAppointments().stream()
                .filter(a -> a.getSchedule().occupancyStart().isBefore(now))
                .collect(Collectors.toSet());

        final Set<Appointment> futureNewAppointments = newAppointments.stream()
                .filter(a -> a.getSchedule().occupancyStart().isAfter(now))
                .collect(Collectors.toSet());

        pastAppointments.addAll(futureNewAppointments);

        bookingUpdates.setAppointments(pastAppointments);
    }

}
