package com.example.booking_api.repository;

import com.example.booking_api.entity.Doctor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DoctorRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Doctor doctor1;
    private Doctor doctor2;

    @BeforeEach
    void setUp() {
        // Очищуємо базу перед кожним тестом
        doctorRepository.deleteAll();

        // Створюємо тестові дані
        doctor1 = new Doctor();
        doctor1.setDoctorName("Др. Коваленко");
        doctor1.setSpecialization("Терапевт");
        doctor1.setEmail("kovalenko@clinic.com");
        doctor1.setPhone("+380501234567");

        doctor2 = new Doctor();
        doctor2.setDoctorName("Др. Шевченко");
        doctor2.setSpecialization("Кардіолог");
        doctor2.setEmail("shevchenko@clinic.com");
        doctor2.setPhone("+380509876543");

        entityManager.persist(doctor1);
        entityManager.persist(doctor2);
        entityManager.flush();
    }

    @Test
    void shouldSaveDoctor() {
        // Given
        Doctor newDoctor = new Doctor();
        newDoctor.setDoctorName("Др. Петренко");
        newDoctor.setSpecialization("Хірург");
        newDoctor.setEmail("petrenko@clinic.com");
        newDoctor.setPhone("+380501111111");

        // When
        Doctor saved = doctorRepository.save(newDoctor);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDoctorName()).isEqualTo("Др. Петренко");
        assertThat(saved.getSpecialization()).isEqualTo("Хірург");
    }

    @Test
    void shouldFindAllDoctors() {
        // When
        List<Doctor> doctors = doctorRepository.findAll();

        // Then
        assertThat(doctors).hasSize(2);
        assertThat(doctors).extracting(Doctor::getDoctorName)
                .containsExactlyInAnyOrder("Др. Коваленко", "Др. Шевченко");
    }

    @Test
    void shouldFindDoctorById() {
        // When
        Optional<Doctor> found = doctorRepository.findById(doctor1.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getDoctorName()).isEqualTo("Др. Коваленко");
    }

    @Test
    void shouldFindDoctorByEmail() {
        // When
        Optional<Doctor> found = doctorRepository.findByEmail("kovalenko@clinic.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getDoctorName()).isEqualTo("Др. Коваленко");
    }

    @Test
    void shouldNotFindDoctorByNonExistentEmail() {
        // When
        Optional<Doctor> found = doctorRepository.findByEmail("nonexistent@clinic.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindDoctorsBySpecialization() {
        // When
        List<Doctor> found = doctorRepository.findBySpecialization("Терапевт");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getDoctorName()).isEqualTo("Др. Коваленко");
    }

    @Test
    void shouldFindDoctorsByNameContaining() {
        // When
        List<Doctor> found = doctorRepository.findByDoctorNameContainingIgnoreCase("коваленко");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getEmail()).isEqualTo("kovalenko@clinic.com");
    }

    @Test
    void shouldFindDoctorsByPartialNameIgnoreCase() {
        // When
        List<Doctor> found = doctorRepository.findByDoctorNameContainingIgnoreCase("шев");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getDoctorName()).isEqualTo("Др. Шевченко");
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        // When
        boolean exists = doctorRepository.existsByEmail("kovalenko@clinic.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        // When
        boolean exists = doctorRepository.existsByEmail("nonexistent@clinic.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void shouldUpdateDoctor() {
        // Given
        Doctor doctor = doctorRepository.findById(doctor1.getId()).orElseThrow();
        doctor.setPhone("+380509999999");

        // When
        Doctor updated = doctorRepository.save(doctor);

        // Then
        assertThat(updated.getPhone()).isEqualTo("+380509999999");
    }

    @Test
    void shouldDeleteDoctor() {
        // Given
        Long doctorId = doctor1.getId();

        // When
        doctorRepository.deleteById(doctorId);

        // Then
        Optional<Doctor> found = doctorRepository.findById(doctorId);
        assertThat(found).isEmpty();
    }

    @Test
    void shouldCountAppointmentsByDoctorId() {
        // Given - потрібно створити appointment для повноцінного тесту
        // Поки що перевіряємо, що метод працює

        // When
        Long count = doctorRepository.countAppointmentsByDoctorId(doctor1.getId());

        // Then
        assertThat(count).isNotNull();
        assertThat(count).isEqualTo(0L); // Поки немає appointments
    }
}
