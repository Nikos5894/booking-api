package com.example.booking_api.dto.validation;

import com.example.booking_api.dto.CreateAppointmentDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalTime;

public class AppointmentTimeSlotValidator
        implements ConstraintValidator<AppointmentTimeSlot, CreateAppointmentDTO> {

    @Override
    public boolean isValid(CreateAppointmentDTO dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getAppointmentTime() == null) {
            return true;
        }

        LocalTime time = dto.getAppointmentTime();

        // Перевірка робочих годин (09:00-18:00)
        LocalTime workStart = LocalTime.of(9, 0);
        LocalTime workEnd = LocalTime.of(18, 0);

        if (time.isBefore(workStart) || time.isAfter(workEnd)) {
            return false;
        }

        // Перевірка кратності 5 хвилинам (09:00, 09:15, 09:30, 09:45)
        int minutes = time.getMinute();
        return minutes % 5 == 0;
    }
}
