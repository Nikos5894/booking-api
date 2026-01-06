package com.example.booking_api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePatientDTO {

    @Size(max = 255, message = "Ім'я пацієнта не може бути довше 255 символів")
    private String patientName;

    @Size(max = 50, message = "Номер телефону не може бути довше 50 символів")
    private String phoneNumber;

    @Email(message = "Невірний формат email")
    @Size(max = 255, message = "Email не може бути довше 255 символів")
    private String email;
}
