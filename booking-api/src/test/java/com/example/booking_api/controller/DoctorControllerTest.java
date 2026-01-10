package com.example.booking_api.controller;

import com.example.booking_api.dto.request.CreateDoctorDTO;
import com.example.booking_api.dto.request.UpdateDoctorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.example.booking_api.entity.Doctor;
import com.example.booking_api.repository.DoctorRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DisplayName("DoctorController Integration Tests")
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoctorRepository doctorRepository;

    private CreateDoctorDTO createDoctorDTO;

    @BeforeEach
    void setUp() {
        doctorRepository.deleteAll();

        createDoctorDTO = new CreateDoctorDTO();
        createDoctorDTO.setDoctorName("Іван Петренко");
        createDoctorDTO.setSpecialization("Кардіолог");
        createDoctorDTO.setEmail("petro@example.com");
        createDoctorDTO.setPhone("+380501234567");
    }

    @Test
    @DisplayName("POST /api/doctors - створення лікаря - успішно")
    void createDoctor_ValidData_ReturnsCreated() throws Exception {
        mockMvc.perform(
                        post("/api/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createDoctorDTO))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.doctorName").value("Іван Петренко"))
                .andExpect(jsonPath("$.specialization").value("Кардіолог"))
                .andExpect(jsonPath("$.email").value("petro@example.com"))
                .andExpect(jsonPath("$.phone").value("+380501234567"))
                .andExpect(jsonPath("$.createdAt").exists());

        assert doctorRepository.count() == 1;
    }

    @Test
    @DisplayName("POST /api/doctors - порожнє ім'я - 400")
    void createDoctor_EmptyName_ReturnsBadRequest() throws Exception {
        createDoctorDTO.setDoctorName("");

        mockMvc.perform(
                        post("/api/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createDoctorDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Помилка валідації"))
                .andExpect(jsonPath("$.errors.doctorName").exists());
    }

    @Test
    @DisplayName("POST /api/doctors - невалідний email - 400")
    void createDoctor_InvalidEmail_ReturnsBadRequest() throws Exception {
        createDoctorDTO.setEmail("invalid-email");

        mockMvc.perform(
                        post("/api/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createDoctorDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").value(containsString("формат")));
    }

    @Test
    @DisplayName("POST /api/doctors - дублікат email - 400")
    void createDoctor_DuplicateEmail_ReturnsBadRequest() throws Exception {
        Doctor existing = new Doctor();
        existing.setDoctorName("Існуючий Лікар");
        existing.setSpecialization("Терапевт");
        existing.setEmail("petro@example.com");
        existing.setPhone("+380501111111");
        doctorRepository.save(existing);

        mockMvc.perform(
                        post("/api/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createDoctorDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("вже існує")));
    }

    @Test
    @DisplayName("GET /api/doctors - отримання всіх лікарів")
    void getAllDoctors_ReturnsListOfDoctors() throws Exception {
        Doctor doctor1 = new Doctor();
        doctor1.setDoctorName("Лікар 1");
        doctor1.setSpecialization("Кардіолог");
        doctor1.setEmail("doc1@example.com");
        doctorRepository.save(doctor1);

        Doctor doctor2 = new Doctor();
        doctor2.setDoctorName("Лікар 2");
        doctor2.setSpecialization("Терапевт");
        doctor2.setEmail("doc2@example.com");
        doctorRepository.save(doctor2);

        mockMvc.perform(get("/api/doctors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].doctorName").value("Лікар 1"))
                .andExpect(jsonPath("$[1].doctorName").value("Лікар 2"));
    }

    @Test
    @DisplayName("GET /api/doctors/{id} - отримання лікаря за ID")
    void getDoctorById_ExistingId_ReturnsDoctor() throws Exception {
        Doctor doctor = new Doctor();
        doctor.setDoctorName("Іван Петренко");
        doctor.setSpecialization("Кардіолог");
        doctor.setEmail("petro@example.com");
        doctor.setPhone("+380501234567");
        Doctor saved = doctorRepository.save(doctor);

        mockMvc.perform(get("/api/doctors/{id}", saved.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.doctorName").value("Іван Петренко"))
                .andExpect(jsonPath("$.email").value("petro@example.com"));
    }

    @Test
    @DisplayName("GET /api/doctors/999 - не знайдено - 404")
    void getDoctorById_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/doctors/{id}", 999L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(containsString("не знайдено")));
    }

    @Test
    @DisplayName("GET /api/doctors/search?name=Іван - пошук за ім'ям")
    void searchDoctorsByName_ReturnsFilteredList() throws Exception {
        Doctor doctor1 = new Doctor();
        doctor1.setDoctorName("Іван Петренко");
        doctor1.setSpecialization("Кардіолог");
        doctor1.setEmail("ivan@example.com");
        doctorRepository.save(doctor1);

        Doctor doctor2 = new Doctor();
        doctor2.setDoctorName("Марія Іванова");
        doctor2.setSpecialization("Терапевт");
        doctor2.setEmail("maria@example.com");
        doctorRepository.save(doctor2);

        mockMvc.perform(get("/api/doctors/search").param("name", "Іван"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("PUT /api/doctors/{id} - оновлення лікаря")
    void updateDoctor_ValidData_ReturnsUpdatedDoctor() throws Exception {
        Doctor doctor = new Doctor();
        doctor.setDoctorName("Старе Ім'я");
        doctor.setSpecialization("Стара Спец");
        doctor.setEmail("old@example.com");
        Doctor saved = doctorRepository.save(doctor);

        UpdateDoctorDTO updateDTO = new UpdateDoctorDTO();
        updateDTO.setDoctorName("Нове Ім'я");
        updateDTO.setSpecialization("Нова Спец");
        updateDTO.setEmail(saved.getEmail());

        mockMvc.perform(
                        put("/api/doctors/{id}", saved.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDTO))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorName").value("Нове Ім'я"))
                .andExpect(jsonPath("$.specialization").value("Нова Спец"));
    }

    @Test
    @DisplayName("DELETE /api/doctors/{id} - видалення лікаря")
    void deleteDoctor_ExistingId_ReturnsNoContent() throws Exception {
        Doctor doctor = new Doctor();
        doctor.setDoctorName("Лікар для видалення");
        doctor.setSpecialization("Терапевт");
        doctor.setEmail("delete@example.com");
        Doctor saved = doctorRepository.save(doctor);

        mockMvc.perform(delete("/api/doctors/{id}", saved.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        assert !doctorRepository.existsById(saved.getId());
    }

    @Test
    @DisplayName("DELETE /api/doctors/{id} - видалення неіснуючого лікаря - 404")
    void deleteDoctor_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/doctors/{id}", 999L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(containsString("не знайдено")));
    }

    @Test
    @DisplayName("PUT /api/doctors/{id} - оновлення лікаря з неіснуючим ID - 404")
    void updateDoctor_NonExistingId_ReturnsNotFound() throws Exception {
        UpdateDoctorDTO updateDTO = new UpdateDoctorDTO();
        updateDTO.setDoctorName("Нове Ім'я");
        updateDTO.setSpecialization("Нова Спец");

        mockMvc.perform(put("/api/doctors/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
