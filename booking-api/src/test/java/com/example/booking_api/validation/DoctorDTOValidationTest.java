package com.example.booking_api.validation;

import com.example.booking_api.dto.request.CreateDoctorDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DoctorDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldPassValidationWithValidData() {
        // Given
        CreateDoctorDTO dto = new CreateDoctorDTO();
        dto.setDoctorName("Др. Коваленко");
        dto.setSpecialization("Терапевт");
        dto.setEmail("test@clinic.com");
        dto.setPhone("+380501234567");

        // When
        Set<ConstraintViolation<CreateDoctorDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidationWhenNameIsNull() {
        // Given
        CreateDoctorDTO dto = new CreateDoctorDTO();
        dto.setDoctorName(null);
        dto.setEmail("test@clinic.com");

        // When
        Set<ConstraintViolation<CreateDoctorDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldFailValidationWhenEmailIsInvalid() {
        // Given
        CreateDoctorDTO dto = new CreateDoctorDTO();
        dto.setDoctorName("Др. Коваленко");
        dto.setEmail("invalid-email");

        // When
        Set<ConstraintViolation<CreateDoctorDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
    }
}
