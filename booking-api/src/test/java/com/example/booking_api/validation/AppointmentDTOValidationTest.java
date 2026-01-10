package com.example.booking_api.validation;

import com.example.booking_api.dto.request.CreateAppointmentDTO;
import com.example.booking_api.dto.request.UpdateAppointmentDTO;
import com.example.booking_api.entity.AppointmentStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тести для валідації Appointment DTO
 */
class AppointmentDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==================== CreateAppointmentDTO Tests ====================

    @Test
    void createAppointment_shouldPassValidation_whenAllFieldsAreValid() {
        // Given
        CreateAppointmentDTO dto = new CreateAppointmentDTO();
        dto.setDoctorId(1L);
        dto.setPatientId(2L);
        dto.setAppointmentDate(LocalDate.now().plusDays(1)); // Future date
        dto.setAppointmentTime(LocalTime.of(10, 0)); // Set a valid time

        // When
        Set<ConstraintViolation<CreateAppointmentDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void createAppointment_shouldFailValidation_whenDoctorIdIsNull() {
        // Given
        CreateAppointmentDTO dto = new CreateAppointmentDTO();
        dto.setDoctorId(null);
        dto.setPatientId(2L);
        dto.setAppointmentDate(LocalDate.now().plusDays(1)); // Future date
        dto.setAppointmentTime(LocalTime.of(10, 0));

        // When
        Set<ConstraintViolation<CreateAppointmentDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("doctorId"));
    }

    @Test
    void createAppointment_shouldFailValidation_whenPatientIdIsNull() {
        // Given
        CreateAppointmentDTO dto = new CreateAppointmentDTO();
        dto.setDoctorId(1L);
        dto.setPatientId(null);
        dto.setAppointmentDate(LocalDate.now().plusDays(1)); // Future date
        dto.setAppointmentTime(LocalTime.of(10, 0));

        // When
        Set<ConstraintViolation<CreateAppointmentDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("patientId"));
    }

    @Test
    void createAppointment_shouldFailValidation_whenAppointmentDateIsNull() {
        // Given
        CreateAppointmentDTO dto = new CreateAppointmentDTO();
        dto.setDoctorId(1L);
        dto.setPatientId(2L);
        dto.setAppointmentDate(null);
        dto.setAppointmentTime(LocalTime.of(10, 0));

        // When
        Set<ConstraintViolation<CreateAppointmentDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("appointmentDate"));
    }

    @Test
    void createAppointment_shouldFailValidation_whenAppointmentDateIsInPast() {
        // Given
        CreateAppointmentDTO dto = new CreateAppointmentDTO();
        dto.setDoctorId(1L);
        dto.setPatientId(2L);
        dto.setAppointmentDate(LocalDate.now().minusDays(1)); // Past date
        dto.setAppointmentTime(LocalTime.of(10, 0));

        // When
        Set<ConstraintViolation<CreateAppointmentDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("appointmentDate"));
    }

    @Test
    void createAppointment_shouldFailValidation_whenAppointmentTimeIsNull() {
        // Given
        CreateAppointmentDTO dto = new CreateAppointmentDTO();
        dto.setDoctorId(1L);
        dto.setPatientId(2L);
        dto.setAppointmentDate(LocalDate.now().plusDays(1)); // Future date
        dto.setAppointmentTime(null);

        // When
        Set<ConstraintViolation<CreateAppointmentDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("appointmentTime"));
    }

    @Test
    void createAppointment_shouldFailValidation_whenStatusIsNull() {
        // Given
        CreateAppointmentDTO dto = new CreateAppointmentDTO();
        dto.setDoctorId(1L);
        dto.setPatientId(2L);
        dto.setAppointmentDate(LocalDate.now().plusDays(1)); // Future date
        dto.setAppointmentTime(LocalTime.of(10, 0));

        // When
        Set<ConstraintViolation<CreateAppointmentDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("status"));
    }

    @Test
    void createAppointment_shouldPassValidation_whenAppointmentDateIsToday() {
        // Given
        CreateAppointmentDTO dto = new CreateAppointmentDTO();
        dto.setDoctorId(1L);
        dto.setPatientId(2L);
        dto.setAppointmentDate(LocalDate.now()); // Today's date
        dto.setAppointmentTime(LocalTime.of(10, 0));

        // When
        Set<ConstraintViolation<CreateAppointmentDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ==================== UpdateAppointmentDTO Tests ====================

    @Test
    void updateAppointment_shouldPassValidation_whenAllFieldsAreValid() {
        // Given
        UpdateAppointmentDTO dto = new UpdateAppointmentDTO();
        dto.setAppointmentDate(LocalDate.now().plusDays(2)); // Future date
        dto.setAppointmentTime(LocalTime.of(14, 30));
        dto.setStatus(AppointmentStatus.CONFIRMED);

        // When
        Set<ConstraintViolation<UpdateAppointmentDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void updateAppointment_shouldPassValidation_whenOnlyStatusIsProvided() {
        // Given - Update дозволяє часткове оновлення
        UpdateAppointmentDTO dto = new UpdateAppointmentDTO();
        dto.setStatus(AppointmentStatus.COMPLETED);

        // When
        Set<ConstraintViolation<UpdateAppointmentDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void updateAppointment_shouldFailValidation_whenAppointmentDateIsInPast() {
        // Given
        UpdateAppointmentDTO dto = new UpdateAppointmentDTO();
        dto.setAppointmentDate(LocalDate.now().minusDays(1)); // Past date
        dto.setAppointmentTime(LocalTime.of(10, 0));

        // When
        Set<ConstraintViolation<UpdateAppointmentDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("appointmentDate"));
    }

    @Test
    void updateAppointment_shouldPassValidation_whenOnlyTimeIsProvided() {
        // Given
        UpdateAppointmentDTO dto = new UpdateAppointmentDTO();
        dto.setAppointmentTime(LocalTime.of(16, 0));

        // When
        Set<ConstraintViolation<UpdateAppointmentDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void updateAppointment_shouldPassValidation_whenAllFieldsAreNull() {
        // Given - Update може бути порожнім (не змінює нічого)
        UpdateAppointmentDTO dto = new UpdateAppointmentDTO();

        // When
        Set<ConstraintViolation<UpdateAppointmentDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }
}
