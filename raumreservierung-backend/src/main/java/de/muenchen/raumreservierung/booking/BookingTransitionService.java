package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.security.SecurityContextService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingTransitionService {

    private final SecurityContextService securityContextService;

    public List<BookingStatus> getPossibleNextStates(final BookingStatus currentStatus) {

        return currentStatus.getTransitions().stream()
                .filter(transition -> securityContextService.hasAuthority(transition.requiredRole()))
                .map(StateTransition::targetStatus)
                .toList();
    }

    public boolean isTransitionAllowed(final BookingStatus currentStatus, final BookingStatus targetStatus) {
        return getPossibleNextStates(currentStatus).contains(targetStatus);
    }

}
