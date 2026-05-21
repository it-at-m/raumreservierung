package de.muenchen.raumreservierung.appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findAllByBookingRoomIdAndScheduleOccupancyStartBetweenOrderByScheduleOccupancyStartAsc(UUID roomId, LocalDateTime start,
            LocalDateTime end);

    List<Appointment> findAllByBookingRoomIdOrderByScheduleOccupancyStartAsc(UUID roomId);
}
