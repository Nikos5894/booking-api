package com.example.booking_api.controller;


import com.example.booking_api.dto.request.CreateAppointmentDTO;
import com.example.booking_api.dto.request.UpdateAppointmentDTO;
import com.example.booking_api.entity.Appointment;
import com.example.booking_api.entity.AppointmentStatus;
import com.example.booking_api.entity.Doctor;
import com.example.booking_api.entity.Patient;
import com.example.booking_api.repository.AppointmentRepository;
import com.example.booking_api.repository.DoctorRepository;
import com.example.booking_api.repository.PatientRepository;
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

import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DisplayName("AppointmentController Integration Tests")
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    private Doctor testDoctor;
    private Patient testPatient;
    private CreateAppointmentDTO createAppointmentDTO;

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();

        testDoctor = new Doctor();
        testDoctor.setDoctorName("Іван Петренко");
        testDoctor.setSpecialization("Кардіолог");
        testDoctor.setEmail("petro@example.com");
        testDoctor.setPhone("+380501234567");
        testDoctor = doctorRepository.save(testDoctor);

        testPatient = new Patient();
        testPatient.setPatientName("Марія Іванова");
        testPatient.setEmail("maria@example.com");
        testPatient.setPhoneNumber("+380509876543");
        testPatient = patientRepository.save(testPatient);

        createAppointmentDTO = new CreateAppointmentDTO();
        createAppointmentDTO.setDoctorId(testDoctor.getId());
        createAppointmentDTO.setPatientId(testPatient.getId());
        createAppointmentDTO.setAppointmentDate(LocalDate.now().plusDays(5));
        createAppointmentDTO.setAppointmentTime(LocalTime.of(14, 0));
    }

    @Test
    @DisplayName("POST /api/appointments - створення запису - успішно")
    void createAppointment_ValidData_ReturnsCreated() throws Exception {
        mockMvc.perform(
                        post("/api/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createAppointmentDTO))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.doctorId").value(testDoctor.getId()))
                .andExpect(jsonPath("$.doctorName").value("Іван Петренко"))
                .andExpect(jsonPath("$.patientId").value(testPatient.getId()))
                .andExpect(jsonPath("$.patientName").value("Марія Іванова"))
                .andExpect(jsonPath("$.appointmentDate").value(createAppointmentDTO.getAppointmentDate().toString()))
                .andExpect(jsonPath("$.appointmentTime").value("14:00:00"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.createdAt").exists());

        long count = appointmentRepository.count();
        assert count == 1 : "У БД має бути 1 запис";
    }

    @Test
    @DisplayName("POST /api/appointments - неіснуючий лікар - 404")
    void createAppointment_DoctorNotFound_ReturnsNotFound() throws Exception {
        createAppointmentDTO.setDoctorId(999L);

        mockMvc.perform(
                        post("/api/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createAppointmentDTO))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(containsString("Лікаря")))
                .andExpect(jsonPath("$.message").value(containsString("не знайдено")));
    }

    @Test
    @DisplayName("POST /api/appointments - неіснуючий пацієнт - 404")
    void createAppointment_PatientNotFound_ReturnsNotFound() throws Exception {
        createAppointmentDTO.setPatientId(999L);

        mockMvc.perform(
                        post("/api/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createAppointmentDTO))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Пацієнта")))
                .andExpect(jsonPath("$.message").value(containsString("не знайдено")));
    }

    @Test
    @DisplayName("POST /api/appointments - час зайнятий - 400")
    void createAppointment_TimeSlotTaken_ReturnsBadRequest() throws Exception {
        Appointment existingAppointment = new Appointment();
        existingAppointment.setDoctor(testDoctor);
        existingAppointment.setPatient(testPatient);
        existingAppointment.setAppointmentDate(createAppointmentDTO.getAppointmentDate());
        existingAppointment.setAppointmentTime(createAppointmentDTO.getAppointmentTime());
        existingAppointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepository.save(existingAppointment);

        mockMvc.perform(
                        post("/api/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createAppointmentDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("зайнятий")));
    }

    @Test
    @DisplayName("POST /api/appointments - минула дата - 400")
    void createAppointment_PastDate_ReturnsBadRequest() throws Exception {
        createAppointmentDTO.setAppointmentDate(LocalDate.now().minusDays(1));

        mockMvc.perform(
                        post("/api/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createAppointmentDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.appointmentDate").exists());
    }

    @Test
    @DisplayName("POST /api/appointments - порожній doctorId - 400")
    void createAppointment_NullDoctorId_ReturnsBadRequest() throws Exception {
        createAppointmentDTO.setDoctorId(null);

        mockMvc.perform(
                        post("/api/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createAppointmentDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.doctorId").exists());
    }

    @Test
    @DisplayName("GET /api/appointments/{id} - отримання запису за ID")
    void getAppointmentById_ExistingId_ReturnsAppointment() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setDoctor(testDoctor);
        appointment.setPatient(testPatient);
        appointment.setAppointmentDate(LocalDate.now().plusDays(3));
        appointment.setAppointmentTime(LocalTime.of(10, 0));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        Appointment saved = appointmentRepository.save(appointment);

        mockMvc.perform(get("/api/appointments/{id}", saved.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.doctorName").value("Іван Петренко"))
                .andExpect(jsonPath("$.patientName").value("Марія Іванова"));
    }

    @Test
    @DisplayName("GET /api/appointments/999 - не знайдено - 404")
    void getAppointmentById_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/appointments/{id}", 999L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Запис з ID 999 не знайдено"));
    }

    @Test
    @DisplayName("GET /api/appointments/doctor/{doctorId} - записи лікаря")
    void getAppointmentsByDoctor_ReturnsListOfAppointments() throws Exception {
        Appointment app1 = new Appointment();
        app1.setDoctor(testDoctor);
        app1.setPatient(testPatient);
        app1.setAppointmentDate(LocalDate.now().plusDays(1));
        app1.setAppointmentTime(LocalTime.of(9, 0));
        app1.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepository.save(app1);

        Appointment app2 = new Appointment();
        app2.setDoctor(testDoctor);
        app2.setPatient(testPatient);
        app2.setAppointmentDate(LocalDate.now().plusDays(2));
        app2.setAppointmentTime(LocalTime.of(10, 0));
        app2.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepository.save(app2);

        mockMvc.perform(get("/api/appointments/doctor/{doctorId}", testDoctor.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].doctorId").value(testDoctor.getId()))
                .andExpect(jsonPath("$[1].doctorId").value(testDoctor.getId()));
    }

    @Test
    @DisplayName("GET /api/appointments/patient/{patientId} - записи пацієнта")
    void getAppointmentsByPatient_ReturnsListOfAppointments() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setDoctor(testDoctor);
        appointment.setPatient(testPatient);
        appointment.setAppointmentDate(LocalDate.now().plusDays(1));
        appointment.setAppointmentTime(LocalTime.of(11, 0));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepository.save(appointment);

        mockMvc.perform(get("/api/appointments/patient/{patientId}", testPatient.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].patientId").value(testPatient.getId()))
                .andExpect(jsonPath("$[0].patientName").value("Марія Іванова"));
    }

    @Test
    @DisplayName("PUT /api/appointments/{id} - оновлення запису")
    void updateAppointment_ValidData_ReturnsUpdatedAppointment() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setDoctor(testDoctor);
        appointment.setPatient(testPatient);
        appointment.setAppointmentDate(LocalDate.now().plusDays(3));
        appointment.setAppointmentTime(LocalTime.of(10, 0));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        Appointment saved = appointmentRepository.save(appointment);

        UpdateAppointmentDTO updateDTO = new UpdateAppointmentDTO();
        updateDTO.setAppointmentDate(LocalDate.now().plusDays(7));
        updateDTO.setAppointmentTime(LocalTime.of(15, 30));
        updateDTO.setStatus(AppointmentStatus.CONFIRMED);

        mockMvc.perform(
                        put("/api/appointments/{id}", saved.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDTO))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.appointmentDate").value(updateDTO.getAppointmentDate().toString()))
                .andExpect(jsonPath("$.appointmentTime").value("15:30:00"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("PATCH /api/appointments/{id}/cancel - скасування запису")
    void cancelAppointment_ExistingId_ReturnsCancelled() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setDoctor(testDoctor);
        appointment.setPatient(testPatient);
        appointment.setAppointmentDate(LocalDate.now().plusDays(5));
        appointment.setAppointmentTime(LocalTime.of(12, 0));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        Appointment saved = appointmentRepository.save(appointment);

        mockMvc.perform(patch("/api/appointments/{id}/cancel", saved.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        Appointment cancelled = appointmentRepository.findById(saved.getId()).orElseThrow();
        assert cancelled.getStatus() == AppointmentStatus.CANCELLED : "Статус має бути CANCELLED";
    }

    @Test
    @DisplayName("PATCH /api/appointments/999/cancel - не знайдено - 404")
    void cancelAppointment_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(patch("/api/appointments/{id}/cancel", 999L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("не знайдено")));
    }

    @Test
    @DisplayName("DELETE /api/appointments/{id} - видалення запису")
    void deleteAppointment_ExistingId_ReturnsNoContent() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setDoctor(testDoctor);
        appointment.setPatient(testPatient);
        appointment.setAppointmentDate(LocalDate.now().plusDays(5));
        appointment.setAppointmentTime(LocalTime.of(12, 0));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        Appointment saved = appointmentRepository.save(appointment);

        mockMvc.perform(patch("/api/appointments/{id}/cancel", saved.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        Appointment cancelled = appointmentRepository.findById(saved.getId()).orElseThrow();
        assert cancelled.getStatus() == AppointmentStatus.CANCELLED : "Статус має бути CANCELLED";
    }
}
