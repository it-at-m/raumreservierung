package de.muenchen.raumreservierung.appointment;

import de.muenchen.raumreservierung.appointment.dto.AppointmentFilterDTO;
import de.muenchen.raumreservierung.appointment.dto.AppointmentMapper;
import de.muenchen.raumreservierung.appointment.dto.AppointmentRequestDTO;
import de.muenchen.raumreservierung.appointment.dto.AppointmentResponseDTO;
import de.muenchen.raumreservierung.appointment.dto.AppointmentSearchResponseDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
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

    //get only for time period and room
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AppointmentSearchResponseDTO> getAppointmentsByPeriodAndRoom(@ParameterObject final AppointmentFilterDTO appointmentFilterDTO) {
        return appointmentService.getAppointmentsByPeriodAndRoom(appointmentFilterDTO).stream().map(appointmentMapper::toSearchDto).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponseDTO createAppointment(@Valid @RequestBody final AppointmentRequestDTO appointmentRequestDTO) {
        return appointmentMapper.toDto(appointmentService.createAppointment(appointmentMapper.toEntity(appointmentRequestDTO)));
    }

    @PutMapping("/{appointmentId}")
    @ResponseStatus(HttpStatus.OK)
    public AppointmentResponseDTO updateAppointment(@Valid @RequestBody final AppointmentRequestDTO appointmentRequestDTO,
            @PathVariable("appointmentId") final UUID appointmentId) {
        return appointmentMapper.toDto(appointmentService.updateAppointment(appointmentMapper.toEntity(appointmentRequestDTO), appointmentId));
    }

    @DeleteMapping("/{appointmentId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAppointment(@Valid @PathVariable("appointmentId") final UUID appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
    }
}
