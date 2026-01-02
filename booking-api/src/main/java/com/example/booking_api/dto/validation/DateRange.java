package com.example.booking_api.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateRange {

    String message() default "Дата початку повинна бути раніше дати закінчення";

    String startField();  // назва поля з початковою датою

    String endField();    // назва поля з кінцевою датою

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
