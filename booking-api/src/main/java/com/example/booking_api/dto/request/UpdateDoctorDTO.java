package com.example.booking_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDoctorDTO {

    @Size(max = 255, message = "Ім'я не повинно перевищувати 255 символів")
    private String doctorName;

    @Size(max = 255, message = "Спеціалізація не повинна перевищувати 255 символів")
    private String specialization;

    @Email(message = "Email повинен мати правильний формат")
    @Size(max = 255, message = "Email не повинен перевищувати 255 символів")
    private String email;

    @Size(max = 50, message = "Номер телефону не повинен перевищувати 50 символів")
    private String phone;
}
