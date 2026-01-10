package com.example.booking_api.repository;

import com.example.booking_api.entity.Appointment;
import com.example.booking_api.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // ========== DERIVED QUERY METHODS ==========

    /**
     * Знайти записи за ID лікаря
     */
    List<Appointment> findByDoctorId(Long doctorId);

    /**
     * Знайти записи за ID пацієнта
     */
    List<Appointment> findByPatientId(Long patientId);

    /**
     * Знайти записи за датою
     */
    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);

    /**
     * Знайти записи за статусом
     */
    List<Appointment> findByStatus(AppointmentStatus status);

    /**
     * Знайти записи між датами
     */
    List<Appointment> findByAppointmentDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Знайти записи лікаря за статусом
     */
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);

    /**
     * Знайти записи пацієнта за статусом
     */
    List<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status);

    // ========== CUSTOM QUERY METHODS ==========

    /**
     * Знайти записи лікаря на певну дату
     */
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date")
    List<Appointment> findByDoctorIdAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    /**
     * Підрахувати кількість записів лікаря
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId")
    Long countByDoctorId(@Param("doctorId") Long doctorId);

    /**
     * Підрахувати кількість записів пацієнта
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patient.id = :patientId")
    Long countByPatientId(@Param("patientId") Long patientId);
}
