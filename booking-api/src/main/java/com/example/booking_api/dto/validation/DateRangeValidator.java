package com.example.booking_api.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {

    private String startFieldName;
    private String endFieldName;

    @Override
    public void initialize(DateRange constraintAnnotation) {
        this.startFieldName = constraintAnnotation.startField();
        this.endFieldName = constraintAnnotation.endField();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        try {
            // Отримуємо значення полів через Reflection
            Field startField = object.getClass().getDeclaredField(startFieldName);
            Field endField = object.getClass().getDeclaredField(endFieldName);

            startField.setAccessible(true);
            endField.setAccessible(true);

            LocalDate startDate = (LocalDate) startField.get(object);
            LocalDate endDate = (LocalDate) endField.get(object);

            // Якщо одне з полів null - валідація проходить
            if (startDate == null || endDate == null) {
                return true;
            }

            // Перевіряємо, що startDate < endDate
            return startDate.isBefore(endDate);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Якщо поля не знайдено - валідація не проходить
            return false;
        }
    }
}
