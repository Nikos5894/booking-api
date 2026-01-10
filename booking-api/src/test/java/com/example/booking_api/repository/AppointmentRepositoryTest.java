package com.example.booking_api.repository;

import com.example.booking_api.entity.Appointment;
import com.example.booking_api.entity.AppointmentStatus;
import com.example.booking_api.entity.Doctor;
import com.example.booking_api.entity.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Doctor doctor;
    private Patient patient;
    private Appointment appointment1;

    @BeforeEach
    void setUp() {
        // Створюємо Doctor
        doctor = new Doctor();
        doctor.setDoctorName("Др. Коваленко");
        doctor.setSpecialization("Терапевт");
        doctor.setEmail("kovalenko@clinic.com");
        doctor.setPhone("+380501234567");
        entityManager.persist(doctor);

        // Створюємо Patient
        patient = new Patient();
        patient.setPatientName("Іван Петренко");
        patient.setEmail("ivan@example.com");
        patient.setPhoneNumber("+380501234567");
        entityManager.persist(patient);

        // Створюємо Appointment
        appointment1 = new Appointment();
        appointment1.setDoctor(doctor);
        appointment1.setPatient(patient);
        appointment1.setAppointmentDate(LocalDate.of(2026, 2, 15));
        appointment1.setAppointmentTime(LocalTime.of(10, 0));
        appointment1.setStatus(AppointmentStatus.SCHEDULED);
        entityManager.persist(appointment1);

        entityManager.flush();
    }

    @Test
    void shouldSaveAppointment() {
        // Given
        Appointment newAppointment = new Appointment();
        newAppointment.setDoctor(doctor);
        newAppointment.setPatient(patient);
        newAppointment.setAppointmentDate(LocalDate.of(2026, 3, 20));
        newAppointment.setAppointmentTime(LocalTime.of(14, 30));
        newAppointment.setStatus(AppointmentStatus.SCHEDULED);

        // When
        Appointment saved = appointmentRepository.save(newAppointment);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDoctor().getDoctorName()).isEqualTo("Др. Коваленко");
        assertThat(saved.getPatient().getPatientName()).isEqualTo("Іван Петренко");
    }

    @Test
    void shouldFindAppointmentsByDoctorId() {
        // When
        List<Appointment> found = appointmentRepository.findByDoctorId(doctor.getId());

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getDoctor().getDoctorName()).isEqualTo("Др. Коваленко");
    }

    @Test
    void shouldFindAppointmentsByPatientId() {
        // When
        List<Appointment> found = appointmentRepository.findByPatientId(patient.getId());

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getPatient().getPatientName()).isEqualTo("Іван Петренко");
    }

    @Test
    void shouldFindAppointmentsByDate() {
        // When
        List<Appointment> found = appointmentRepository.findByAppointmentDate(LocalDate.of(2026, 2, 15));

        // Then
        assertThat(found).hasSize(1);
    }

    @Test
    void shouldFindAppointmentsByStatus() {
        // When
        List<Appointment> found = appointmentRepository.findByStatus(AppointmentStatus.SCHEDULED);

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
    }

    @Test
    void shouldUpdateAppointmentStatus() {
        // Given
        Appointment appointment = appointmentRepository.findById(appointment1.getId()).orElseThrow();

        // When
        appointment.setStatus(AppointmentStatus.COMPLETED);
        Appointment updated = appointmentRepository.save(appointment);

        // Then
        assertThat(updated.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }

    @Test
    void shouldDeleteAppointment() {
        // Given
        Long appointmentId = appointment1.getId();

        // When
        appointmentRepository.deleteById(appointmentId);

        // Then
        assertThat(appointmentRepository.findById(appointmentId)).isEmpty();
    }
}
