package com.example.booking_api.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
        // Ініціалізація (якщо потрібна)
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        // null вважається валідним (використовуйте @NotNull окремо)
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return true;
        }

        // Перевірка формату: +380XXXXXXXXX або 0XXXXXXXXX
        return phoneNumber.matches("^(\\+380|0)[0-9]{9}$");
    }
}
