package com.example.booking_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {

    private Long id;

    private String patientName;

    private String phoneNumber;

    private String email;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
