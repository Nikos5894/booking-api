package com.example.booking_api.validation;

import com.example.booking_api.dto.request.CreatePatientDTO;
import com.example.booking_api.dto.request.UpdatePatientDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тести для валідації Patient DTO
 */
class PatientDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==================== CreatePatientDTO Tests ====================

    @Test
    void createPatient_shouldPassValidation_whenAllFieldsAreValid() {
        CreatePatientDTO dto = new CreatePatientDTO();
        dto.setPatientName("Іван Петренко");
        dto.setEmail("ivan@example.com");
        dto.setPhoneNumber("+380501234567");

        // When
        Set<ConstraintViolation<CreatePatientDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void createPatient_shouldFailValidation_whenPatientNameIsNull() {
        // Given
        CreatePatientDTO dto = new CreatePatientDTO();
        dto.setPatientName(null);
        dto.setEmail("test@example.com");
        dto.setPhoneNumber("+380501234567");

        // When
        Set<ConstraintViolation<CreatePatientDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("patientName"));
    }

    @Test
    void createPatient_shouldFailValidation_whenPatientNameIsBlank() {
        // Given
        CreatePatientDTO dto = new CreatePatientDTO();
        dto.setPatientName("   ");
        dto.setEmail("test@example.com");
        dto.setPhoneNumber("+380501234567");

        // When
        Set<ConstraintViolation<CreatePatientDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    void createPatient_shouldFailValidation_whenEmailIsInvalid() {

        CreatePatientDTO dto = new CreatePatientDTO();
        dto.setPatientName("Іван Петренко");
        dto.setEmail("invalid-email");
        dto.setPhoneNumber("+380501234567");

        // When
        Set<ConstraintViolation<CreatePatientDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void createPatient_shouldFailValidation_whenEmailIsNull() {
        CreatePatientDTO dto = new CreatePatientDTO();
        dto.setPatientName("Іван Петренко");
        dto.setEmail(null);
        dto.setPhoneNumber("+380501234567");

        // When
        Set<ConstraintViolation<CreatePatientDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    void createPatient_shouldPassValidation_whenPhoneNumberIsNull() {
        CreatePatientDTO dto = new CreatePatientDTO();
        dto.setPatientName("Іван Петренко");
        dto.setEmail("ivan@example.com");
        dto.setPhoneNumber(null);

        // When
        Set<ConstraintViolation<CreatePatientDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void createPatient_shouldPassValidation_whenDateOfBirthIsNull() {
        CreatePatientDTO dto = new CreatePatientDTO();
        dto.setPatientName("Іван Петренко");
        dto.setEmail("ivan@example.com");
        dto.setPhoneNumber("+380501234567");

        // When
        Set<ConstraintViolation<CreatePatientDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void createPatient_shouldFailValidation_whenDateOfBirthIsInFuture() {
        // Given
        CreatePatientDTO dto = new CreatePatientDTO();
        dto.setPatientName("Іван Петренко");
        dto.setEmail("ivan@example.com");
        dto.setPhoneNumber("+380501234567");

        // When
        Set<ConstraintViolation<CreatePatientDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("dateOfBirth"));
    }

    // ==================== UpdatePatientDTO Tests ====================

    @Test
    void updatePatient_shouldPassValidation_whenAllFieldsAreValid() {
        // Given
        UpdatePatientDTO dto = new UpdatePatientDTO();
        dto.setPatientName("Марія Коваленко");
        dto.setEmail("maria@example.com");
        dto.setPhoneNumber("+380509876543");

        // When
        Set<ConstraintViolation<UpdatePatientDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void updatePatient_shouldPassValidation_whenOnlyOneFieldIsProvided() {
        // Given - Update дозволяє часткове оновлення
        UpdatePatientDTO dto = new UpdatePatientDTO();
        dto.setPhoneNumber("+380509999999");

        // When
        Set<ConstraintViolation<UpdatePatientDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void updatePatient_shouldFailValidation_whenEmailIsInvalid() {
        // Given
        UpdatePatientDTO dto = new UpdatePatientDTO();
        dto.setEmail("invalid-email");

        // When
        Set<ConstraintViolation<UpdatePatientDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void updatePatient_shouldFailValidation_whenPatientNameIsBlank() {
        // Given
        UpdatePatientDTO dto = new UpdatePatientDTO();
        dto.setPatientName("   ");

        // When
        Set<ConstraintViolation<UpdatePatientDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
    }

}
