package com.example.booking_api.integration;

import com.example.booking_api.dto.request.CreateDoctorDTO;
import com.example.booking_api.dto.response.DoctorDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Doctor API Integration Tests (PostgreSQL)")
class DoctorApiIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("POST /api/doctors - створення лікаря через API + PostgreSQL")
    void shouldCreateDoctorViaApi() {
        // Given
        CreateDoctorDTO request = new CreateDoctorDTO();
        request.setDoctorName("Др. Коваленко");
        request.setSpecialization("Терапевт");
        request.setEmail("kovalenko@clinic.com");
        request.setPhone("+380501234567");

        // When
        ResponseEntity<DoctorDTO> response = restTemplate.postForEntity(
                "/api/doctors",
                request,
                DoctorDTO.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getDoctorName()).isEqualTo("Др. Коваленко");
        assertThat(response.getBody().getSpecialization()).isEqualTo("Терапевт");
    }

    @Test
    @DisplayName("GET /api/doctors - отримання всіх лікарів через API + PostgreSQL")
    void shouldGetAllDoctorsViaApi() {
        // Given - створюємо лікаря
        CreateDoctorDTO request = new CreateDoctorDTO();
        request.setDoctorName("Др. Шевченко");
        request.setSpecialization("Кардіолог");
        request.setEmail("shevchenko@clinic.com");
        request.setPhone("+380509876543");
        restTemplate.postForEntity("/api/doctors", request, DoctorDTO.class);

        // When
        ResponseEntity<DoctorDTO[]> response = restTemplate.getForEntity(
                "/api/doctors",
                DoctorDTO[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("GET /api/doctors/999 - не знайдено - 404")
    void shouldReturn404WhenDoctorNotFound() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/doctors/999",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
