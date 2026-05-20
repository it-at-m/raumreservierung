package de.muenchen.raumreservierung.appointment;

import de.muenchen.raumreservierung.appointment.dto.AppointmentDetailsResponseDTO;
import de.muenchen.raumreservierung.appointment.dto.AppointmentFilterDTO;
import de.muenchen.raumreservierung.appointment.dto.AppointmentMapper;
import de.muenchen.raumreservierung.appointment.dto.AppointmentRequestDTO;
import de.muenchen.raumreservierung.appointment.dto.AppointmentResponseDTO;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    private final AppointmentMapper appointmentMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AppointmentDetailsResponseDTO> getAppointmentsByPeriodAndRoom(@ParameterObject final AppointmentFilterDTO appointmentFilterDTO) {
        return appointmentService.getAppointmentsByPeriodAndRoom(appointmentFilterDTO).stream().map(appointmentMapper::toSearchDto).toList();
    }

    @PutMapping("/{appointmentId}")
    @ResponseStatus(HttpStatus.OK)
    public AppointmentResponseDTO updateAppointment(@Valid @RequestBody final AppointmentRequestDTO appointmentRequestDTO,
            @PathVariable final UUID appointmentId) {
        return appointmentMapper.toDto(appointmentService.updateAppointment(appointmentMapper.toEntity(appointmentRequestDTO), appointmentId));
    }

    @DeleteMapping("/{appointmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAppointment(@PathVariable @Valid final UUID appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
    }
}
