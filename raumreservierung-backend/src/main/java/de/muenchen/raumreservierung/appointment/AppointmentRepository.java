package de.muenchen.raumreservierung.appointment;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findAllByBookingRoomIdAndScheduleOccupancyStartBetween(UUID roomId, OffsetDateTime start, OffsetDateTime end);
}
