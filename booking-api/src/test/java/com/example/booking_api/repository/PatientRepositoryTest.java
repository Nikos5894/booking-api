package com.example.booking_api.repository;

import com.example.booking_api.entity.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Patient patient1;
    private Patient patient2;

    @BeforeEach
    void setUp() {
        // Очищуємо базу перед кожним тестом
        patientRepository.deleteAll();

        // Створюємо тестові дані
        patient1 = new Patient();
        patient1.setPatientName("Іван Петренко");
        patient1.setEmail("ivan@example.com");
        patient1.setPhoneNumber("+380501234567");

        patient2 = new Patient();
        patient2.setPatientName("Марія Коваленко");
        patient2.setEmail("maria@example.com");
        patient2.setPhoneNumber("+380509876543");

        entityManager.persist(patient1);
        entityManager.persist(patient2);
        entityManager.flush();
    }

    @Test
    void shouldSavePatient() {
        // Given
        Patient newPatient = new Patient();
        newPatient.setPatientName("Олександр Шевченко");
        newPatient.setEmail("oleksandr@example.com");
        newPatient.setPhoneNumber("+380501111111");

        // When
        Patient saved = patientRepository.save(newPatient);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPatientName()).isEqualTo("Олександр Шевченко");
        assertThat(saved.getEmail()).isEqualTo("oleksandr@example.com");
    }

    @Test
    void shouldFindAllPatients() {
        // When
        List<Patient> patients = patientRepository.findAll();

        // Then
        assertThat(patients).hasSize(2);
        assertThat(patients).extracting(Patient::getPatientName)
                .containsExactlyInAnyOrder("Іван Петренко", "Марія Коваленко");
    }

    @Test
    void shouldFindPatientById() {
        // When
        Optional<Patient> found = patientRepository.findById(patient1.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getPatientName()).isEqualTo("Іван Петренко");
    }

    @Test
    void shouldFindPatientByEmail() {
        // When
        Optional<Patient> found = patientRepository.findByEmail("ivan@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getPatientName()).isEqualTo("Іван Петренко");
    }

    @Test
    void shouldNotFindPatientByNonExistentEmail() {
        // When
        Optional<Patient> found = patientRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindPatientByPhoneNumber() {
        // When
        Optional<Patient> found = patientRepository.findByPhoneNumber("+380501234567");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getPatientName()).isEqualTo("Іван Петренко");
    }

    @Test
    void shouldNotFindPatientByNonExistentPhoneNumber() {
        // When
        Optional<Patient> found = patientRepository.findByPhoneNumber("+380500000000");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindPatientsByNameContaining() {
        // When
        List<Patient> found = patientRepository.findByPatientNameContainingIgnoreCase("петренко");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getEmail()).isEqualTo("ivan@example.com");
    }

    @Test
    void shouldFindPatientsByPartialNameIgnoreCase() {
        // When
        List<Patient> found = patientRepository.findByPatientNameContainingIgnoreCase("мар");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getPatientName()).isEqualTo("Марія Коваленко");
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        // When
        boolean exists = patientRepository.existsByEmail("ivan@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        // When
        boolean exists = patientRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void shouldReturnTrueWhenPhoneNumberExists() {
        // When
        boolean exists = patientRepository.existsByPhoneNumber("+380501234567");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenPhoneNumberDoesNotExist() {
        // When
        boolean exists = patientRepository.existsByPhoneNumber("+380500000000");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void shouldUpdatePatient() {
        // Given
        Patient patient = patientRepository.findById(patient1.getId()).orElseThrow();
        patient.setPhoneNumber("+380509999999");

        // When
        Patient updated = patientRepository.save(patient);

        // Then
        assertThat(updated.getPhoneNumber()).isEqualTo("+380509999999");
    }

    @Test
    void shouldDeletePatient() {
        // Given
        Long patientId = patient1.getId();

        // When
        patientRepository.deleteById(patientId);

        // Then
        Optional<Patient> found = patientRepository.findById(patientId);
        assertThat(found).isEmpty();
    }

    @Test
    void shouldCountAppointmentsByPatientId() {
        // Given - потрібно створити appointment для повноцінного тесту
        // Поки що перевіряємо, що метод працює

        // When
        Long count = patientRepository.countAppointmentsByPatientId(patient1.getId());

        // Then
        assertThat(count).isNotNull();
        assertThat(count).isEqualTo(0L); // Поки немає appointments
    }
}
