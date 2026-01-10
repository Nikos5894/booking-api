package com.example.booking_api.repository;

import com.example.booking_api.entity.Appointment;
import com.example.booking_api.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // ========== DERIVED QUERY METHODS ==========

    /**
     * Знайти всі записи для лікаря
     */
    List<Appointment> findByDoctorId(Long doctorId);

    /**
     * Знайти всі записи для пацієнта
     */
    List<Appointment> findByPatientId(Long patientId);

    /**
     * Знайти записи за статусом
     */
    List<Appointment> findByStatus(AppointmentStatus status);

    /**
     * Знайти записи лікаря на конкретну дату
     */
    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);

    /**
     * Знайти записи пацієнта за статусом
     */
    List<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status);

    /**
     * Перевірити чи існує запис лікаря на конкретний час
     */
    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTime(
            Long doctorId,
            LocalDate date,
            LocalTime time
    );

    /**
     * Знайти записи за діапазоном дат
     */
    List<Appointment> findByAppointmentDateBetween(LocalDate startDate, LocalDate endDate);

    // ========== CUSTOM QUERY METHODS ==========

    /**
     * Знайти майбутні записи лікаря
     */
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
            "AND a.appointmentDate >= :today " +
            "ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
    List<Appointment> findUpcomingAppointmentsByDoctor(
            @Param("doctorId") Long doctorId,
            @Param("today") LocalDate today
    );

    /**
     * Знайти майбутні записи пацієнта
     */
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId " +
            "AND a.appointmentDate >= :today " +
            "ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
    List<Appointment> findUpcomingAppointmentsByPatient(
            @Param("patientId") Long patientId,
            @Param("today") LocalDate today
    );

    /**
     * Знайти історію записів пацієнта
     */
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId " +
            "AND (a.appointmentDate < :today OR a.status = 'COMPLETED') " +
            "ORDER BY a.appointmentDate DESC, a.appointmentTime DESC")
    List<Appointment> findAppointmentHistory(
            @Param("patientId") Long patientId,
            @Param("today") LocalDate today
    );

    /**
     * Знайти записи за статусом та діапазоном дат
     */
    @Query("SELECT a FROM Appointment a WHERE a.status = :status " +
            "AND a.appointmentDate BETWEEN :startDate AND :endDate " +
            "ORDER BY a.appointmentDate DESC")
    List<Appointment> findByStatusAndDateRange(
            @Param("status") AppointmentStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Підрахувати кількість записів лікаря на дату
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId " +
            "AND a.appointmentDate = :date")
    Long countAppointmentsByDoctorAndDate(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date
    );

    /**
     * Знайти зайняті слоти лікаря на дату
     */
    @Query("SELECT a.appointmentTime FROM Appointment a WHERE a.doctor.id = :doctorId " +
            "AND a.appointmentDate = :date " +
            "AND a.status != 'CANCELLED'")
    List<LocalTime> findBookedTimeSlots(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date
    );

    /**
     * Пошук записів з деталями (оптимізований)
     */
    @Query("SELECT a FROM Appointment a " +
            "JOIN FETCH a.doctor " +
            "JOIN FETCH a.patient " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.appointmentDate = :date " +
            "AND a.status = :status")
    List<Appointment> findAppointmentsWithDetails(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("status") AppointmentStatus status
    );
}
