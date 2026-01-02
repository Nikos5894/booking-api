package com.example.booking_api.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AppointmentTimeSlotValidator.class)
@Target({ElementType.TYPE})  // ← для КЛАСУ, не поля!
@Retention(RetentionPolicy.RUNTIME)
public @interface AppointmentTimeSlot {

    String message() default "Час прийому повинен бути в робочі години (09:00-18:00) і кратний 5 хвилинам";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
