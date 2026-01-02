package com.example.booking_api.dto;

import com.example.booking_api.entity.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {

    private Long id;

    private Long doctorId;

    private String doctorName;

    private Long patientId;

    private String patientName;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    private AppointmentStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
