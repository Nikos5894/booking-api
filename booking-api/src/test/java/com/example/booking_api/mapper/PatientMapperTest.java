package com.example.booking_api.mapper;

import com.example.booking_api.dto.request.CreatePatientDTO;
import com.example.booking_api.dto.request.UpdatePatientDTO;
import com.example.booking_api.dto.response.PatientDTO;
import com.example.booking_api.entity.Patient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PatientMapperTest {

    private final PatientMapper mapper = new PatientMapper();

    @Test
    void shouldConvertCreateDTOToEntity() {
        // Given
        CreatePatientDTO dto = new CreatePatientDTO();
        dto.setPatientName("Пац. Коваленко");
        dto.setEmail("testPatient@gmail.com");
        dto.setPhoneNumber("+380501234567");

        // When
        Patient entity = mapper.toEntity(dto);

        // Then
        assertThat(entity.getPatientName()).isEqualTo("Др. Коваленко");
        assertThat(entity.getEmail()).isEqualTo("testPatient@gmail.com");
    }

    @Test
    void shouldConvertEntityToDTO() {
        // Given
        Patient entity = new Patient();
        entity.setId(1L);
        entity.setPatientName("Пац. Шевченко");
        entity.setEmail("shevchenko@clinic.com");
        entity.setPhoneNumber("+380509876543");

        // When
        PatientDTO dto = mapper.toDTO(entity);

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getPatientName()).isEqualTo("Пац. Шевченко");
    }

    @Test
    void shouldUpdateEntityFromUpdateDTO() {
        // Given
        Patient entity = new Patient();
        entity.setPatientName("Старе ім'я");
        entity.setEmail("testPatient@gmail.com");

        UpdatePatientDTO dto = new UpdatePatientDTO();
        dto.setPatientName("Нове ім'я");
        dto.setEmail("patient@gmail.com");

        // When
        mapper.updateEntity(entity, dto);

        // Then
        assertThat(entity.getPatientName()).isEqualTo("Нове ім'я");
        assertThat(entity.getEmail()).isEqualTo("patient@gmail.com");
    }
}
