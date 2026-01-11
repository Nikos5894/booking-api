package com.example.booking_api.service;

import com.example.booking_api.dto.request.CreatePatientDTO;
import com.example.booking_api.dto.request.UpdatePatientDTO;
import com.example.booking_api.dto.response.PatientDTO;
import com.example.booking_api.entity.Patient;
import com.example.booking_api.exception.NotFoundException;
import com.example.booking_api.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    // ========== CREATE ==========

    @Transactional
    public PatientDTO createPatient(CreatePatientDTO dto) {
        // Перевірка унікальності email
        if (patientRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException(
                    "Пацієнт з email " + dto.getEmail() + " вже існує"
            );
        }

        // Перевірка унікальності номера телефону (якщо вказаний)
        if (dto.getPhoneNumber() != null &&
                patientRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new IllegalArgumentException(
                    "Пацієнт з номером телефону " + dto.getPhoneNumber() + " вже існує"
            );
        }

        // Створення сутності
        Patient patient = new Patient();
        patient.setPatientName(dto.getPatientName());
        patient.setPhoneNumber(dto.getPhoneNumber());
        patient.setEmail(dto.getEmail());

        Patient saved = patientRepository.save(patient);
        return convertToDTO(saved);
    }

    // ========== READ ==========

    public Patient findByUserId(Long userId) {
        return patientRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Patient", "userId", userId));
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Пацієнта з ID " + id + " не знайдено"
                ));
        return convertToDTO(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientByEmail(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Пацієнта з email " + email + " не знайдено"
                ));
        return convertToDTO(patient);
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientByPhoneNumber(String phoneNumber) {
        Patient patient = patientRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Пацієнта з номером телефону " + phoneNumber + " не знайдено"
                ));
        return convertToDTO(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> searchPatientsByName(String name) {
        return patientRepository.findByPatientNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== UPDATE ==========

    @Transactional
    public PatientDTO updatePatient(Long id, UpdatePatientDTO dto) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Пацієнта з ID " + id + " не знайдено"
                ));

        // Оновлюємо тільки ті поля, що передані
        if (dto.getPatientName() != null) {
            patient.setPatientName(dto.getPatientName());
        }

        if (dto.getEmail() != null) {
            // Перевіряємо унікальність нового email
            if (!dto.getEmail().equals(patient.getEmail()) &&
                    patientRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException(
                        "Email " + dto.getEmail() + " вже використовується"
                );
            }
            patient.setEmail(dto.getEmail());
        }

        if (dto.getPhoneNumber() != null) {
            // Перевіряємо унікальність нового номера телефону
            if (!dto.getPhoneNumber().equals(patient.getPhoneNumber()) &&
                    patientRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
                throw new IllegalArgumentException(
                        "Номер телефону " + dto.getPhoneNumber() + " вже використовується"
                );
            }
            patient.setPhoneNumber(dto.getPhoneNumber());
        }

        Patient updated = patientRepository.save(patient);
        return convertToDTO(updated);
    }

    // ========== DELETE ==========

    @Transactional
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Пацієнта з ID " + id + " не знайдено"
            );
        }

        // Перевіряємо чи є активні записи
        Long appointmentsCount = patientRepository.countAppointmentsByPatientId(id);
        if (appointmentsCount > 0) {
            throw new IllegalStateException(
                    "Неможливо видалити пацієнта. У нього є " + appointmentsCount + " активних записів"
            );
        }

        patientRepository.deleteById(id);
    }

    // ========== ДОПОМІЖНІ МЕТОДИ ==========

    private PatientDTO convertToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setPatientName(patient.getPatientName());
        dto.setPhoneNumber(patient.getPhoneNumber());
        dto.setEmail(patient.getEmail());
        dto.setCreatedAt(patient.getCreatedAt());
        dto.setUpdatedAt(patient.getUpdatedAt());
        return dto;
    }
}
