package com.example.booking_api.dto;

import com.example.booking_api.dto.validation.AppointmentTimeSlot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@AppointmentTimeSlot
public class CreateAppointmentDTO {

    @NotNull(message = "ID лікаря обов'язковий")
    private Long doctorId;

    @NotNull(message = "ID пацієнта обов'язковий")
    private Long patientId;

    @NotNull(message = "Дата прийому обов'язкова")
    @Future(message = "Дата прийому повинна бути в майбутньому")
    private LocalDate appointmentDate;

    @NotNull(message = "Час прийому обов'язковий")
    private LocalTime appointmentTime;
}
