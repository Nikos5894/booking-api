package com.example.booking_api.service;

import com.example.booking_api.dto.response.AppointmentDTO;
import com.example.booking_api.dto.request.CreateAppointmentDTO;
import com.example.booking_api.dto.request.UpdateAppointmentDTO;
import com.example.booking_api.entity.Appointment;
import com.example.booking_api.entity.AppointmentStatus;
import com.example.booking_api.entity.Doctor;
import com.example.booking_api.entity.Patient;
import com.example.booking_api.repository.AppointmentRepository;
import com.example.booking_api.repository.DoctorRepository;
import com.example.booking_api.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit тести для AppointmentService
 * Тестуємо бізнес-логіку без Spring Context та БД
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentService Unit Tests")
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Doctor testDoctor;
    private Patient testPatient;
    private Appointment testAppointment;
    private CreateAppointmentDTO createDTO;
    private UpdateAppointmentDTO updateDTO;

    @BeforeEach
    void setUp() {
        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setDoctorName("Іван Петренко");
        testDoctor.setSpecialization("Кардіолог");
        testDoctor.setEmail("petro@example.com");
        testDoctor.setPhone("+380501234567");
        testDoctor.setCreatedAt(LocalDateTime.now());

        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setPatientName("Марія Іванова");
        testPatient.setEmail("maria@example.com");
        testPatient.setPhoneNumber("+380509876543");
        testPatient.setCreatedAt(LocalDateTime.now());

        testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setDoctor(testDoctor);
        testAppointment.setPatient(testPatient);
        testAppointment.setAppointmentDate(LocalDate.now().plusDays(5));
        testAppointment.setAppointmentTime(LocalTime.of(14, 0));
        testAppointment.setStatus(AppointmentStatus.SCHEDULED);
        testAppointment.setCreatedAt(LocalDateTime.now());

        createDTO = new CreateAppointmentDTO();
        createDTO.setDoctorId(1L);
        createDTO.setPatientId(1L);
        createDTO.setAppointmentDate(LocalDate.now().plusDays(5));
        createDTO.setAppointmentTime(LocalTime.of(14, 0));

        updateDTO = new UpdateAppointmentDTO();
        updateDTO.setAppointmentDate(LocalDate.now().plusDays(7));
        updateDTO.setAppointmentTime(LocalTime.of(15, 30));
        updateDTO.setStatus(AppointmentStatus.CONFIRMED);
    }

    // ========== CREATE TESTS ==========

    @Test
    @DisplayName("Створення запису - успішно")
    void createAppointment_ValidData_Success() {
        // Arrange
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTime(
                anyLong(), any(LocalDate.class), any(LocalTime.class)))
                .thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // Act
        AppointmentDTO result = appointmentService.createAppointment(createDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDoctorId()).isEqualTo(1L);
        assertThat(result.getDoctorName()).isEqualTo("Іван Петренко");
        assertThat(result.getPatientId()).isEqualTo(1L);
        assertThat(result.getPatientName()).isEqualTo("Марія Іванова");
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);

        // Verify
        verify(doctorRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Створення запису - лікар не знайдений")
    void createAppointment_DoctorNotFound_ThrowsException() {
        // Arrange
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> appointmentService.createAppointment(createDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Лікаря")
                .hasMessageContaining("не знайдено");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Створення запису - пацієнт не знайдений")
    void createAppointment_PatientNotFound_ThrowsException() {
        // Arrange
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> appointmentService.createAppointment(createDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Пацієнта")
                .hasMessageContaining("не знайдено");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Створення запису - час зайнятий")
    void createAppointment_TimeSlotTaken_ThrowsException() {
        // Arrange
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTime(
                anyLong(), any(LocalDate.class), any(LocalTime.class)))
                .thenReturn(true);  // ❌ Час зайнятий!

        // Act & Assert
        assertThatThrownBy(() -> appointmentService.createAppointment(createDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("зайнятий");

        verify(appointmentRepository, never()).save(any());
    }

    // ========== READ TESTS ==========

    @Test
    @DisplayName("Отримання запису за ID - успішно")
    void getAppointmentById_ExistingId_ReturnsAppointment() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

        // Act
        AppointmentDTO result = appointmentService.getAppointmentById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDoctorName()).isEqualTo("Іван Петренко");
        assertThat(result.getPatientName()).isEqualTo("Марія Іванова");

        verify(appointmentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Отримання запису за ID - не знайдено")
    void getAppointmentById_NonExistingId_ThrowsException() {
        // Arrange
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> appointmentService.getAppointmentById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не знайдено");

        verify(appointmentRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Отримання записів за лікарем")
    void getAppointmentsByDoctor_ReturnsListOfAppointments() {
        // Arrange
        Appointment appointment2 = new Appointment();
        appointment2.setId(2L);
        appointment2.setDoctor(testDoctor);
        appointment2.setPatient(testPatient);
        appointment2.setAppointmentDate(LocalDate.now().plusDays(6));
        appointment2.setAppointmentTime(LocalTime.of(15, 0));
        appointment2.setStatus(AppointmentStatus.SCHEDULED);
        appointment2.setCreatedAt(LocalDateTime.now());

        List<Appointment> appointments = Arrays.asList(testAppointment, appointment2);
        when(appointmentRepository.findByDoctorId(1L)).thenReturn(appointments);

        // Act
        List<AppointmentDTO> result = appointmentService.getAppointmentsByDoctor(1L);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDoctorId()).isEqualTo(1L);
        assertThat(result.get(1).getDoctorId()).isEqualTo(1L);

        verify(appointmentRepository, times(1)).findByDoctorId(1L);
    }

    @Test
    @DisplayName("Отримання записів за пацієнтом")
    void getAppointmentsByPatient_ReturnsListOfAppointments() {
        // Arrange
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findByPatientId(1L)).thenReturn(appointments);

        // Act
        List<AppointmentDTO> result = appointmentService.getAppointmentsByPatient(1L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPatientId()).isEqualTo(1L);
        assertThat(result.get(0).getPatientName()).isEqualTo("Марія Іванова");

        verify(appointmentRepository, times(1)).findByPatientId(1L);
    }

    // ========== UPDATE TESTS ==========

    @Test
    @DisplayName("Оновлення запису - успішно")
    void updateAppointment_ValidData_ReturnsUpdatedAppointment() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // Act
        AppointmentDTO result = appointmentService.updateAppointment(1L, updateDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Оновлення запису - не знайдено")
    void updateAppointment_NonExistingId_ThrowsException() {
        // Arrange
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> appointmentService.updateAppointment(999L, updateDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не знайдено");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Оновлення запису - часткове оновлення (тільки дата)")
    void updateAppointment_PartialUpdate_OnlyUpdatesProvidedFields() {
        // Arrange
        UpdateAppointmentDTO partialUpdate = new UpdateAppointmentDTO();
        partialUpdate.setAppointmentDate(LocalDate.now().plusDays(10));
        // appointmentTime та status = null (не оновлюємо)

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // Act
        AppointmentDTO result = appointmentService.updateAppointment(1L, partialUpdate);

        // Assert
        assertThat(result).isNotNull();
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    // ========== CANCEL TESTS ==========

    @Test
    @DisplayName("Скасування запису - успішно")
    void cancelAppointment_ExistingId_SetsCancelledStatus() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // Act
        appointmentService.cancelAppointment(1L);

        // Assert
        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Скасування запису - не знайдено")
    void cancelAppointment_NonExistingId_ThrowsException() {
        // Arrange
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> appointmentService.cancelAppointment(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не знайдено");

        verify(appointmentRepository, never()).save(any());
    }

    // ========== EDGE CASES ==========

    @Test
    @DisplayName("Створення запису - null DTO")
    void createAppointment_NullDTO_ThrowsException() {
        assertThatThrownBy(() -> appointmentService.createAppointment(null))
                .isInstanceOf(NullPointerException.class);

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Отримання записів лікаря - порожній список")
    void getAppointmentsByDoctor_NoAppointments_ReturnsEmptyList() {
        when(appointmentRepository.findByDoctorId(1L)).thenReturn(Arrays.asList());

        List<AppointmentDTO> result = appointmentService.getAppointmentsByDoctor(1L);

        assertThat(result).isEmpty();
        verify(appointmentRepository, times(1)).findByDoctorId(1L);
    }

    @Test
    @DisplayName("Отримання записів пацієнта - порожній список")
    void getAppointmentsByPatient_NoAppointments_ReturnsEmptyList() {
        when(appointmentRepository.findByPatientId(1L)).thenReturn(Arrays.asList());

        List<AppointmentDTO> result = appointmentService.getAppointmentsByPatient(1L);

        assertThat(result).isEmpty();
        verify(appointmentRepository, times(1)).findByPatientId(1L);
    }

    @Test
    @DisplayName("Оновлення - всі поля null")
    void updateAppointment_AllFieldsNull_NoChanges() {
        UpdateAppointmentDTO emptyUpdate = new UpdateAppointmentDTO();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        AppointmentDTO result = appointmentService.updateAppointment(1L, emptyUpdate);

        assertThat(result).isNotNull();
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Конвертація Entity в DTO - правильні дані")
    void convertToDTO_ValidEntity_ReturnsCorrectDTO() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

        AppointmentDTO result = appointmentService.getAppointmentById(1L);

        assertThat(result.getId()).isEqualTo(testAppointment.getId());
        assertThat(result.getDoctorId()).isEqualTo(testDoctor.getId());
        assertThat(result.getPatientId()).isEqualTo(testPatient.getId());
        assertThat(result.getAppointmentDate()).isEqualTo(testAppointment.getAppointmentDate());
        assertThat(result.getAppointmentTime()).isEqualTo(testAppointment.getAppointmentTime());
        assertThat(result.getStatus()).isEqualTo(testAppointment.getStatus());
    }
}

