package com.example.booking_api.repository;

import com.example.booking_api.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // ========== DERIVED QUERY METHODS ==========

    /**
     * Знайти лікаря за email
     */
    Optional<Doctor> findByEmail(String email);
    Optional<Doctor> findByUserId(Long userId);


    /**
     * Знайти лікарів за спеціалізацією
     */
    List<Doctor> findBySpecialization(String specialization);

    /**
     * Знайти лікарів за частиною імені (регістронезалежний пошук)
     */
    List<Doctor> findByDoctorNameContainingIgnoreCase(String name);

    /**
     * Перевірити чи існує лікар з таким email
     */
    boolean existsByEmail(String email);

    // ========== CUSTOM QUERY METHODS ==========

    /**
     * Знайти всіх лікарів певної спеціалізації з сортуванням за ім'ям
     */
    @Query("SELECT d FROM Doctor d WHERE d.specialization = :specialization ORDER BY d.doctorName ASC")
    List<Doctor> findDoctorsBySpecializationSorted(@Param("specialization") String specialization);

    /**
     * Знайти лікарів, які мають записи на певну дату
     */
    @Query("SELECT DISTINCT d FROM Doctor d " +
            "JOIN d.appointments a " +
            "WHERE a.appointmentDate = :date")
    List<Doctor> findDoctorsWithAppointmentsOnDate(@Param("date") java.time.LocalDate date);

    /**
     * Підрахувати кількість записів лікаря
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId")
    Long countAppointmentsByDoctorId(@Param("doctorId") Long doctorId);

    /**
     * Знайти найзавантаженіших лікарів (топ N за кількістю записів)
     */
    @Query("SELECT d FROM Doctor d " +
            "JOIN d.appointments a " +
            "GROUP BY d " +
            "ORDER BY COUNT(a) DESC")
    List<Doctor> findMostBookedDoctors();
}
