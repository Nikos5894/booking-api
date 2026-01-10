package com.example.booking_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {

    private Long id;

    private String doctorName;

    private String specialization;

    private String email;

    private String phone;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}