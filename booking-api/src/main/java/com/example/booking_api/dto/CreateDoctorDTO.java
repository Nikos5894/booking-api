package com.example.booking_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDoctorDTO {

    @NotBlank(message = "Ім'я лікаря обов'язкове")
    @Size(max = 255, message = "Ім'я лікаря не може бути довше 255 символів")
    private String doctorName;

    @Size(max = 255, message = "Спеціалізація не може бути довше 255 символів")
    private String specialization;
}
