package com.example.booking_api.service;

import com.example.booking_api.dto.request.CreateDoctorDTO;
import com.example.booking_api.dto.request.UpdateDoctorDTO;
import com.example.booking_api.dto.response.DoctorDTO;
import com.example.booking_api.entity.Doctor;
import com.example.booking_api.repository.DoctorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    // ========== CREATE ==========

    @Transactional
    public DoctorDTO createDoctor(CreateDoctorDTO dto) {
        // Перевірка унікальності email
        if (dto.getEmail() != null && doctorRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException(
                    "Лікар з email " + dto.getEmail() + " вже існує"
            );
        }

        // Створення сутності
        Doctor doctor = new Doctor();
        doctor.setDoctorName(dto.getDoctorName());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setEmail(dto.getEmail());
        doctor.setPhone(dto.getPhone());

        Doctor saved = doctorRepository.save(doctor);
        return convertToDTO(saved);
    }

    // ========== READ ==========

    @Transactional(readOnly = true)
    public DoctorDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Лікаря з ID " + id + " не знайдено"
                ));
        return convertToDTO(doctor);
    }

    @Transactional(readOnly = true)
    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DoctorDTO getDoctorByEmail(String email) {
        Doctor doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Лікаря з email " + email + " не знайдено"
                ));
        return convertToDTO(doctor);
    }

    @Transactional(readOnly = true)
    public List<DoctorDTO> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DoctorDTO> searchDoctorsByName(String name) {
        return doctorRepository.findByDoctorNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== UPDATE ==========

    @Transactional
    public DoctorDTO updateDoctor(Long id, UpdateDoctorDTO dto) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Лікаря з ID " + id + " не знайдено"
                ));

        // Оновлюємо тільки ті поля, що передані
        if (dto.getDoctorName() != null) {
            doctor.setDoctorName(dto.getDoctorName());
        }

        if (dto.getSpecialization() != null) {
            doctor.setSpecialization(dto.getSpecialization());
        }

        if (dto.getEmail() != null) {
            // Перевіряємо унікальність нового email
            if (!dto.getEmail().equals(doctor.getEmail()) &&
                    doctorRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException(
                        "Email " + dto.getEmail() + " вже використовується"
                );
            }
            doctor.setEmail(dto.getEmail());
        }

        if (dto.getPhone() != null) {
            doctor.setPhone(dto.getPhone());
        }

        doctor.setUpdatedAt(LocalDateTime.now());
        Doctor updated = doctorRepository.save(doctor);
        return convertToDTO(updated);
    }

    // ========== DELETE ==========

    @Transactional
    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Лікаря з ID " + id + " не знайдено"
            );
        }

        // Перевіряємо чи є активні записи
        Long appointmentsCount = doctorRepository.countAppointmentsByDoctorId(id);
        if (appointmentsCount > 0) {
            throw new IllegalStateException(
                    "Неможливо видалити лікаря. У нього є " + appointmentsCount + " активних записів"
            );
        }

        doctorRepository.deleteById(id);
    }

    // ========== ДОПОМІЖНІ МЕТОДИ ==========

    private DoctorDTO convertToDTO(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setDoctorName(doctor.getDoctorName());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setEmail(doctor.getEmail());
        dto.setPhone(doctor.getPhone());
        dto.setCreatedAt(doctor.getCreatedAt());
        dto.setUpdatedAt(doctor.getUpdatedAt());
        return dto;
    }
}
