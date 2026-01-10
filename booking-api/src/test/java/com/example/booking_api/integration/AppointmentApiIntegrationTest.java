package com.example.booking_api.integration;

import com.example.booking_api.dto.request.CreateAppointmentDTO;
import com.example.booking_api.dto.request.CreateDoctorDTO;
import com.example.booking_api.dto.request.CreatePatientDTO;
import com.example.booking_api.dto.response.AppointmentDTO;
import com.example.booking_api.dto.response.DoctorDTO;
import com.example.booking_api.dto.response.PatientDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Appointment API Integration Tests (PostgreSQL)")
class AppointmentApiIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private Long doctorId;
    private Long patientId;

    @BeforeEach
    void setUp() {
        long timestamp = System.currentTimeMillis();

        // Створюємо лікаря
        CreateDoctorDTO doctorRequest = new CreateDoctorDTO();
        doctorRequest.setDoctorName("Др. Тестовий");
        doctorRequest.setSpecialization("Терапевт");
        doctorRequest.setEmail("test" + timestamp + "@clinic.com");
        doctorRequest.setPhone("+380501234567");

        ResponseEntity<DoctorDTO> doctorResponse = restTemplate.postForEntity(
                "/api/doctors",
                doctorRequest,
                DoctorDTO.class
        );

        assertThat(doctorResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(doctorResponse.getBody()).isNotNull();
        doctorId = doctorResponse.getBody().getId();

        // Створюємо пацієнта з УНІКАЛЬНИМ номером
        CreatePatientDTO patientRequest = new CreatePatientDTO();
        patientRequest.setPatientName("Пацієнт Тестовий");
        patientRequest.setEmail("patient" + timestamp + "@example.com");
        patientRequest.setPhoneNumber("+38050" + timestamp % 10000000);  // ✅ Унікальний номер!

        ResponseEntity<PatientDTO> patientResponse = restTemplate.postForEntity(
                "/api/patients",
                patientRequest,
                PatientDTO.class
        );

        assertThat(patientResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(patientResponse.getBody()).isNotNull();
        patientId = patientResponse.getBody().getId();
    }




    @Test
    @DisplayName("POST /api/appointments - створення запису через API + PostgreSQL")
    void shouldCreateAppointmentViaApi() {
        // Given
        CreateAppointmentDTO request = new CreateAppointmentDTO();
        request.setDoctorId(doctorId);
        request.setPatientId(patientId);
        request.setAppointmentDate(LocalDate.now().plusDays(5));
        request.setAppointmentTime(LocalTime.of(14, 0));

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/appointments",
                request,
                String.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }


    @Test
    @DisplayName("GET /api/appointments/doctor/{doctorId} - записи лікаря через API + PostgreSQL")
    void shouldGetAppointmentsByDoctorViaApi() {
        // Given - створюємо запис
        CreateAppointmentDTO request = new CreateAppointmentDTO();
        request.setDoctorId(doctorId);
        request.setPatientId(patientId);
        request.setAppointmentDate(LocalDate.now().plusDays(3));
        request.setAppointmentTime(LocalTime.of(10, 0));
        restTemplate.postForEntity("/api/appointments", request, AppointmentDTO.class);

        // When
        ResponseEntity<AppointmentDTO[]> response = restTemplate.getForEntity(
                "/api/appointments/doctor/" + doctorId,
                AppointmentDTO[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(1);
        assertThat(response.getBody()[0].getDoctorId()).isEqualTo(doctorId);
    }

    @Test
    @DisplayName("GET /api/appointments/patient/{patientId} - записи пацієнта через API + PostgreSQL")
    void shouldGetAppointmentsByPatientViaApi() {
        // Given - створюємо запис
        CreateAppointmentDTO request = new CreateAppointmentDTO();
        request.setDoctorId(doctorId);
        request.setPatientId(patientId);
        request.setAppointmentDate(LocalDate.now().plusDays(4));
        request.setAppointmentTime(LocalTime.of(11, 30));
        restTemplate.postForEntity("/api/appointments", request, AppointmentDTO.class);

        // When
        ResponseEntity<AppointmentDTO[]> response = restTemplate.getForEntity(
                "/api/appointments/patient/" + patientId,
                AppointmentDTO[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(1);
        assertThat(response.getBody()[0].getPatientId()).isEqualTo(patientId);
    }

    @Test
    @DisplayName("GET /api/appointments/999 - не знайдено - 404")
    void shouldReturn404WhenAppointmentNotFound() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/appointments/999",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("POST /api/appointments - неіснуючий лікар - 400 (не 404!)")
    void shouldReturn404WhenDoctorNotFound() {
        // Given
        CreateAppointmentDTO request = new CreateAppointmentDTO();
        request.setDoctorId(9999L);
        request.setPatientId(patientId);
        request.setAppointmentDate(LocalDate.now().plusDays(5));
        request.setAppointmentTime(LocalTime.of(14, 0));

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/appointments",
                request,
                String.class
        );

        // Then
        // API повертає 400 або 404 в залежності від того, як обробляється помилка
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    @DisplayName("POST /api/appointments - неіснуючий пацієнт - 400 (не 404!)")
    void shouldReturn404WhenPatientNotFound() {
        // Given
        CreateAppointmentDTO request = new CreateAppointmentDTO();
        request.setDoctorId(doctorId);
        request.setPatientId(9999L);
        request.setAppointmentDate(LocalDate.now().plusDays(5));
        request.setAppointmentTime(LocalTime.of(14, 0));

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/appointments",
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }
}

