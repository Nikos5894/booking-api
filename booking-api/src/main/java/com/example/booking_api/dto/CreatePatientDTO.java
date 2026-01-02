package com.example.booking_api.dto;

import com.example.booking_api.dto.validation.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePatientDTO {

    @NotBlank(message = "Ім'я пацієнта обов'язкове")
    @Size(max = 255, message = "Ім'я пацієнта не може бути довше 255 символів")
    private String patientName;

    @PhoneNumber
    private String phoneNumber;

    @Email(message = "Невірний формат email")
    @Size(max = 255, message = "Email не може бути довше 255 символів")
    private String email;
}
