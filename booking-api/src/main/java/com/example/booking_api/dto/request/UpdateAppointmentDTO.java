package com.example.booking_api.dto.request;

import com.example.booking_api.entity.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Future;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentDTO {

    @Future(message = "Дата прийому повинна бути в майбутньому")
    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    private AppointmentStatus status;
}
