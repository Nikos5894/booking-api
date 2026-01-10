package com.example.booking_api.integration;

import com.example.booking_api.dto.request.CreatePatientDTO;
import com.example.booking_api.dto.response.PatientDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Patient API Integration Tests (PostgreSQL)")
class PatientApiIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("POST /api/patients - створення пацієнта через API + PostgreSQL")
    void shouldCreatePatientViaApi() {
        // Given
        CreatePatientDTO request = new CreatePatientDTO();
        request.setPatientName("Іван Петренко");
        request.setEmail("ivan@example.com");
        request.setPhoneNumber("+380501234567");

        // When
        ResponseEntity<PatientDTO> response = restTemplate.postForEntity(
                "/api/patients",
                request,
                PatientDTO.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getPatientName()).isEqualTo("Іван Петренко");
    }

    @Test
    @DisplayName("GET /api/patients - отримання всіх пацієнтів через API + PostgreSQL")
    void shouldGetAllPatientsViaApi() {
        // When
        ResponseEntity<PatientDTO[]> response = restTemplate.getForEntity(
                "/api/patients",
                PatientDTO[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}
