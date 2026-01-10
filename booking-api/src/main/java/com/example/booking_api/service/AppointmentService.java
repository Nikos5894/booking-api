package com.example.booking_api.service;


import com.example.booking_api.dto.request.CreateAppointmentDTO;
import com.example.booking_api.dto.request.UpdateAppointmentDTO;
import com.example.booking_api.dto.response.AppointmentDTO;
import com.example.booking_api.entity.Appointment;
import com.example.booking_api.entity.AppointmentStatus;
import com.example.booking_api.entity.Doctor;
import com.example.booking_api.entity.Patient;
import com.example.booking_api.repository.AppointmentRepository;
import com.example.booking_api.repository.DoctorRepository;
import com.example.booking_api.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor  // Lombok створить конструктор автоматично
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public AppointmentDTO createAppointment(CreateAppointmentDTO dto) {
        // 1. Перевірка чи існує Doctor
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Лікаря з ID " + dto.getDoctorId() + " не знайдено"
                ));

        // 2. Перевірка чи існує Patient
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Пацієнта з ID " + dto.getPatientId() + " не знайдено"
                ));

        // 3. Перевірка чи час вільний
        boolean timeSlotTaken = appointmentRepository
                .existsByDoctorIdAndAppointmentDateAndAppointmentTime(
                        dto.getDoctorId(),
                        dto.getAppointmentDate(),
                        dto.getAppointmentTime()
                );

        if (timeSlotTaken) {
            throw new IllegalArgumentException(
                    "Цей час вже зайнятий. Оберіть інший час."
            );
        }

        // 4. Створення сутності Appointment
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setAppointmentTime(dto.getAppointmentTime());
        appointment.setStatus(AppointmentStatus.SCHEDULED);  // Початковий статус
        appointment.setCreatedAt(LocalDateTime.now());

        // 5. Збереження в БД
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // 6. Конвертація в DTO для відповіді
        return convertToDTO(savedAppointment);
    }

    @Transactional(readOnly = true)
    public AppointmentDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Запис з ID " + id + " не знайдено"
                ));

        return convertToDTO(appointment);
    }

    @Transactional
    public AppointmentDTO updateAppointment(Long id, UpdateAppointmentDTO dto) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Запис з ID " + id + " не знайдено"
                ));

        // Оновлення полів якщо вони передані
        if (dto.getAppointmentDate() != null) {
            appointment.setAppointmentDate(dto.getAppointmentDate());
        }

        if (dto.getAppointmentTime() != null) {
            appointment.setAppointmentTime(dto.getAppointmentTime());
        }

        if (dto.getStatus() != null) {
            appointment.setStatus(dto.getStatus());
        }

        appointment.setUpdatedAt(LocalDateTime.now());

        Appointment updated = appointmentRepository.save(appointment);
        return convertToDTO(updated);
    }

    @Transactional
    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Запис з ID " + id + " не знайдено"
                ));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setUpdatedAt(LocalDateTime.now());

        appointmentRepository.save(appointment);
    }

    // Допоміжний метод для конвертації Entity -> DTO
    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setDoctorName(appointment.getDoctor().getDoctorName());
        dto.setPatientId(appointment.getPatient().getId());
        dto.setPatientName(appointment.getPatient().getPatientName());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setAppointmentTime(appointment.getAppointmentTime());
        dto.setStatus(appointment.getStatus());
        dto.setCreatedAt(appointment.getCreatedAt());
        dto.setUpdatedAt(appointment.getUpdatedAt());

        return dto;
    }
}
