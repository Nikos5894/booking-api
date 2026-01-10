package com.example.booking_api.mapper;

import com.example.booking_api.dto.request.CreateDoctorDTO;
import com.example.booking_api.dto.request.UpdateDoctorDTO;
import com.example.booking_api.dto.response.DoctorDTO;
import com.example.booking_api.entity.Doctor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DoctorMapperTest {

    private final DoctorMapper mapper = new DoctorMapper();

    @Test
    void shouldConvertCreateDTOToEntity() {
        // Given
        CreateDoctorDTO dto = new CreateDoctorDTO();
        dto.setDoctorName("Др. Коваленко");
        dto.setSpecialization("Терапевт");
        dto.setEmail("test@clinic.com");
        dto.setPhone("+380501234567");

        // When
        Doctor entity = mapper.toEntity(dto);

        // Then
        assertThat(entity.getDoctorName()).isEqualTo("Др. Коваленко");
        assertThat(entity.getSpecialization()).isEqualTo("Терапевт");
        assertThat(entity.getEmail()).isEqualTo("test@clinic.com");
    }

    @Test
    void shouldConvertEntityToDTO() {
        // Given
        Doctor entity = new Doctor();
        entity.setId(1L);
        entity.setDoctorName("Др. Шевченко");
        entity.setSpecialization("Кардіолог");
        entity.setEmail("shevchenko@clinic.com");
        entity.setPhone("+380509876543");

        // When
        DoctorDTO dto = mapper.toDTO(entity);

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDoctorName()).isEqualTo("Др. Шевченко");
        assertThat(dto.getSpecialization()).isEqualTo("Кардіолог");
    }

    @Test
    void shouldUpdateEntityFromUpdateDTO() {
        // Given
        Doctor entity = new Doctor();
        entity.setDoctorName("Старе ім'я");
        entity.setEmail("old@clinic.com");

        UpdateDoctorDTO dto = new UpdateDoctorDTO();
        dto.setDoctorName("Нове ім'я");
        dto.setEmail("new@clinic.com");

        // When
        mapper.updateEntity(entity, dto);

        // Then
        assertThat(entity.getDoctorName()).isEqualTo("Нове ім'я");
        assertThat(entity.getEmail()).isEqualTo("new@clinic.com");
    }
}
