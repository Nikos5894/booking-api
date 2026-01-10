package com.example.booking_api.service;

import com.example.booking_api.dto.request.CreateDoctorDTO;
import com.example.booking_api.dto.request.UpdateDoctorDTO;
import com.example.booking_api.dto.response.DoctorDTO;
import com.example.booking_api.entity.Doctor;
import com.example.booking_api.exception.NotFoundException;
import com.example.booking_api.mapper.DoctorMapper;
import com.example.booking_api.repository.DoctorRepository;
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
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorMapper doctorMapper;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor doctor;
    private CreateDoctorDTO createDoctorDTO;
    private UpdateDoctorDTO updateDoctorDTO;
    private DoctorDTO doctorDTO;

    @BeforeEach
    void setUp() {
        // Підготовка тестових даних
        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setDoctorName("Др. Коваленко");
        doctor.setSpecialization("Терапевт");
        doctor.setEmail("kovalenko@clinic.com");
        doctor.setPhone("+380501234567");

        createDoctorDTO = new CreateDoctorDTO();
        createDoctorDTO.setDoctorName("Др. Коваленко");
        createDoctorDTO.setSpecialization("Терапевт");
        createDoctorDTO.setEmail("kovalenko@clinic.com");
        createDoctorDTO.setPhone("+380501234567");

        updateDoctorDTO = new UpdateDoctorDTO();
        updateDoctorDTO.setDoctorName("Др. Коваленко");
        updateDoctorDTO.setSpecialization("Терапевт");
        updateDoctorDTO.setEmail("kovalenko@clinic.com");
        updateDoctorDTO.setPhone("+380501234567");

        doctorDTO = new DoctorDTO();
        doctorDTO.setId(1L);
        doctorDTO.setDoctorName("Др. Коваленко");
        doctorDTO.setSpecialization("Терапевт");
        doctorDTO.setEmail("kovalenko@clinic.com");
        doctorDTO.setPhone("+380501234567");
    }

    @Test
    void createDoctor_Success() {
        // Given
        when(doctorMapper.toEntity(any(CreateDoctorDTO.class))).thenReturn(doctor);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);
        when(doctorMapper.toDTO(any(Doctor.class))).thenReturn(doctorDTO);

        // When
        DoctorDTO result = doctorService.createDoctor(createDoctorDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDoctorName()).isEqualTo("Др. Коваленко");
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    void getAllDoctors_Success() {
        // Given
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));
        when(doctorMapper.toDTO(any(Doctor.class))).thenReturn(doctorDTO);

        // When
        List<DoctorDTO> result = doctorService.getAllDoctors();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDoctorName()).isEqualTo("Др. Коваленко");
        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    void getDoctorById_Success() {
        // Given
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorMapper.toDTO(any(Doctor.class))).thenReturn(doctorDTO);

        // When
        DoctorDTO result = doctorService.getDoctorById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(doctorRepository, times(1)).findById(1L);
    }

    @Test
    void getDoctorById_NotFound() {
        // Given
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> doctorService.getDoctorById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Doctor not found with id: 999");

        verify(doctorRepository, times(1)).findById(999L);
    }

    @Test
    void updateDoctor_Success() {
        // Given
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);
        when(doctorMapper.toDTO(any(Doctor.class))).thenReturn(doctorDTO);

        // When
        DoctorDTO result = doctorService.updateDoctor(1L, updateDoctorDTO);

        // Then
        assertThat(result).isNotNull();
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    void deleteDoctor_Success() {
        // Given
        when(doctorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(doctorRepository).deleteById(1L);

        // When
        doctorService.deleteDoctor(1L);

        // Then
        verify(doctorRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteDoctor_NotFound() {
        // Given
        when(doctorRepository.existsById(anyLong())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> doctorService.deleteDoctor(999L))
                .isInstanceOf(NotFoundException.class);

        verify(doctorRepository, never()).deleteById(anyLong());
    }
}
