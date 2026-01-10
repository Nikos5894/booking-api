package com.example.booking_api.controller;


import com.example.booking_api.dto.request.CreatePatientDTO;
import com.example.booking_api.dto.request.UpdatePatientDTO;
import com.example.booking_api.entity.Patient;
import com.example.booking_api.repository.PatientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("PatientController Integration Tests")
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    private CreatePatientDTO createPatientDTO;

    @BeforeEach
    void setUp() {
        patientRepository.deleteAll();

        createPatientDTO = new CreatePatientDTO();
        createPatientDTO.setPatientName("Марія Іванова");
        createPatientDTO.setEmail("maria@example.com");
        createPatientDTO.setPhoneNumber("+380509876543");
    }

    @Test
    @DisplayName("POST /api/patients - створення пацієнта - успішно")
    void createPatient_ValidData_ReturnsCreated() throws Exception {
        mockMvc.perform(
                        post("/api/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createPatientDTO))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.patientName").value("Марія Іванова"))
                .andExpect(jsonPath("$.email").value("maria@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("+380509876543"))
                .andExpect(jsonPath("$.createdAt").exists());

        assert patientRepository.count() == 1;
    }

    @Test
    @DisplayName("POST /api/patients - порожнє ім'я - 400")
    void createPatient_EmptyName_ReturnsBadRequest() throws Exception {
        createPatientDTO.setPatientName("");

        mockMvc.perform(
                        post("/api/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createPatientDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.patientName").exists());
    }

    @Test
    @DisplayName("POST /api/patients - невалідний email - 400")
    void createPatient_InvalidEmail_ReturnsBadRequest() throws Exception {
        createPatientDTO.setEmail("not-an-email");

        mockMvc.perform(
                        post("/api/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createPatientDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").value(containsString("формат")));
    }

    @Test
    @DisplayName("POST /api/patients - дублікат email - 400")
    void createPatient_DuplicateEmail_ReturnsBadRequest() throws Exception {
        Patient existing = new Patient();
        existing.setPatientName("Існуючий Пацієнт");
        existing.setEmail("maria@example.com");
        existing.setPhoneNumber("+380501111111");
        patientRepository.save(existing);

        mockMvc.perform(
                        post("/api/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createPatientDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("вже існує")));
    }

    @Test
    @DisplayName("GET /api/patients - отримання всіх пацієнтів")
    void getAllPatients_ReturnsListOfPatients() throws Exception {
        Patient patient1 = new Patient();
        patient1.setPatientName("Пацієнт 1");
        patient1.setEmail("patient1@example.com");
        patient1.setPhoneNumber("+380501111111");
        patientRepository.save(patient1);

        Patient patient2 = new Patient();
        patient2.setPatientName("Пацієнт 2");
        patient2.setEmail("patient2@example.com");
        patient2.setPhoneNumber("+380502222222");
        patientRepository.save(patient2);

        mockMvc.perform(get("/api/patients"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].patientName").value("Пацієнт 1"))
                .andExpect(jsonPath("$[1].patientName").value("Пацієнт 2"));
    }

    @Test
    @DisplayName("GET /api/patients/{id} - отримання пацієнта за ID")
    void getPatientById_ExistingId_ReturnsPatient() throws Exception {
        Patient patient = new Patient();
        patient.setPatientName("Марія Іванова");
        patient.setEmail("maria@example.com");
        patient.setPhoneNumber("+380509876543");
        Patient saved = patientRepository.save(patient);

        mockMvc.perform(get("/api/patients/{id}", saved.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.patientName").value("Марія Іванова"))
                .andExpect(jsonPath("$.email").value("maria@example.com"));
    }

    @Test
    @DisplayName("GET /api/patients/999 - не знайдено - 404")
    void getPatientById_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/patients/{id}", 999L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(containsString("не знайдено")));
    }

    @Test
    @DisplayName("PUT /api/patients/{id} - оновлення пацієнта")
    void updatePatient_ValidData_ReturnsUpdatedPatient() throws Exception {
        Patient patient = new Patient();
        patient.setPatientName("Старе Ім'я");
        patient.setEmail("old@example.com");
        patient.setPhoneNumber("+380501111111");
        Patient saved = patientRepository.save(patient);

        UpdatePatientDTO updateDTO = new UpdatePatientDTO();
        updateDTO.setPatientName("Нове Ім'я");
        updateDTO.setEmail("new@example.com");

        mockMvc.perform(
                        put("/api/patients/{id}", saved.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDTO))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientName").value("Нове Ім'я"))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("DELETE /api/patients/{id} - видалення пацієнта")
    void deletePatient_ExistingId_ReturnsNoContent() throws Exception {
        Patient patient = new Patient();
        patient.setPatientName("Пацієнт для видалення");
        patient.setEmail("delete@example.com");
        patient.setPhoneNumber("+380509999999");
        Patient saved = patientRepository.save(patient);

        mockMvc.perform(delete("/api/patients/{id}", saved.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        assert !patientRepository.existsById(saved.getId());
    }
}
