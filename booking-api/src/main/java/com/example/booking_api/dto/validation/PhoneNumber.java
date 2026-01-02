package com.example.booking_api.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {

    String message() default "Невірний формат телефону. Використовуйте: +380XXXXXXXXX або 0XXXXXXXXX";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
