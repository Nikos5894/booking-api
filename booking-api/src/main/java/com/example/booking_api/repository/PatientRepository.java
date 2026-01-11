package com.example.booking_api.repository;

import com.example.booking_api.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // ========== DERIVED QUERY METHODS ==========

    /**
     * Знайти пацієнта за email
     */
    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByUserId(Long userId);

    /**
     * Знайти пацієнта за номером телефону
     */
    Optional<Patient> findByPhoneNumber(String phoneNumber);

    /**
     * Знайти пацієнтів за частиною імені (регістронезалежний пошук)
     */
    List<Patient> findByPatientNameContainingIgnoreCase(String name);

    /**
     * Перевірити чи існує пацієнт з таким email
     */
    boolean existsByEmail(String email);

    /**
     * Перевірити чи існує пацієнт з таким номером телефону
     */
    boolean existsByPhoneNumber(String phoneNumber);

    // ========== CUSTOM QUERY METHODS ==========

    /**
     * Знайти пацієнтів, які мають записи на певну дату
     */
    @Query("SELECT DISTINCT p FROM Patient p " +
            "JOIN p.appointments a " +
            "WHERE a.appointmentDate = :date")
    List<Patient> findPatientsWithAppointmentsOnDate(@Param("date") java.time.LocalDate date);

    /**
     * Підрахувати кількість записів пацієнта
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patient.id = :patientId")
    Long countAppointmentsByPatientId(@Param("patientId") Long patientId);

    /**
     * Знайти пацієнтів, які мали записи у певного лікаря
     */
    @Query("SELECT DISTINCT p FROM Patient p " +
            "JOIN p.appointments a " +
            "WHERE a.doctor.id = :doctorId")
    List<Patient> findPatientsByDoctorId(@Param("doctorId") Long doctorId);
}
