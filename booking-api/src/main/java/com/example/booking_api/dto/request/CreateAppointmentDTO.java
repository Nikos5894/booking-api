package com.example.booking_api.dto.request;

import com.example.booking_api.dto.validation.AppointmentTimeSlot;
import com.example.booking_api.entity.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
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
    @FutureOrPresent(message = "Дата прийому повинна бути в майбутньому або сьогодні")
    private LocalDate appointmentDate;

    @NotNull(message = "Час прийому обов'язковий")
    private LocalTime appointmentTime;

}
