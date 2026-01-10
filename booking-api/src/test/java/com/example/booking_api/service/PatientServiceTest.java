package com.example.booking_api.service;

import com.example.booking_api.dto.request.CreatePatientDTO;
import com.example.booking_api.dto.request.UpdatePatientDTO;
import com.example.booking_api.dto.response.PatientDTO;
import com.example.booking_api.entity.Patient;
import com.example.booking_api.exception.NotFoundException;
import com.example.booking_api.mapper.PatientMapper;
import com.example.booking_api.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;
    private CreatePatientDTO createPatientDTO;
    private UpdatePatientDTO updatePatientDTO;
    private PatientDTO patientDTO;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setPatientName("Іван Петренко");
        patient.setEmail("ivan@example.com");
        patient.setPhoneNumber("+380501234567");

        createPatientDTO = new CreatePatientDTO();
        createPatientDTO.setPatientName("Іван Петренко");
        createPatientDTO.setEmail("ivan@example.com");
        createPatientDTO.setPhoneNumber("+380501234567");

        updatePatientDTO = new UpdatePatientDTO();
        updatePatientDTO.setPatientName("Іван Петренко");
        updatePatientDTO.setEmail("ivan@example.com");
        updatePatientDTO.setPhoneNumber("+380501234567");

        patientDTO = new PatientDTO();
        patientDTO.setId(1L);
        patientDTO.setPatientName("Іван Петренко");
        patientDTO.setEmail("ivan@example.com");
        patientDTO.setPhoneNumber("+380501234567");
    }

    @Test
    void createPatient_Success() {
        // Given
        when(patientMapper.toEntity(any(CreatePatientDTO.class))).thenReturn(patient);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(patientMapper.toDTO(any(Patient.class))).thenReturn(patientDTO);

        // When
        PatientDTO result = patientService.createPatient(createPatientDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPatientName()).isEqualTo("Іван Петренко");
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void getAllPatients_Success() {
        // Given
        when(patientRepository.findAll()).thenReturn(List.of(patient));
        when(patientMapper.toDTO(any(Patient.class))).thenReturn(patientDTO);

        // When
        List<PatientDTO> result = patientService.getAllPatients();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPatientName()).isEqualTo("Іван Петренко");
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void getPatientById_Success() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientMapper.toDTO(any(Patient.class))).thenReturn(patientDTO);

        // When
        PatientDTO result = patientService.getPatientById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void getPatientById_NotFound() {
        // Given
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> patientService.getPatientById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Patient not found with id: 999");

        verify(patientRepository, times(1)).findById(999L);
    }

    @Test
    void updatePatient_Success() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(patientMapper.toDTO(any(Patient.class))).thenReturn(patientDTO);

        // When
        PatientDTO result = patientService.updatePatient(1L, updatePatientDTO);

        // Then
        assertThat(result).isNotNull();
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void deletePatient_Success() {
        // Given
        when(patientRepository.existsById(1L)).thenReturn(true);
        doNothing().when(patientRepository).deleteById(1L);

        // When
        patientService.deletePatient(1L);

        // Then
        verify(patientRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletePatient_NotFound() {
        // Given
        when(patientRepository.existsById(anyLong())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> patientService.deletePatient(999L))
                .isInstanceOf(NotFoundException.class);

        verify(patientRepository, never()).deleteById(anyLong());
    }
}
