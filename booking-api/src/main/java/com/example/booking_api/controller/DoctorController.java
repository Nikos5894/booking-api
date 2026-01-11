package com.example.booking_api.controller;


import com.example.booking_api.dto.request.CreateDoctorDTO;
import com.example.booking_api.dto.request.UpdateDoctorDTO;
import com.example.booking_api.dto.response.DoctorDTO;
import com.example.booking_api.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    /**
     * POST /api/doctors - Створити лікаря
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN'")
    public ResponseEntity<DoctorDTO> createDoctor(@Valid @RequestBody CreateDoctorDTO dto) {
        DoctorDTO created = doctorService.createDoctor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /api/doctors - Отримати всіх лікарів
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        List<DoctorDTO> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    /**
     * GET /api/doctors/{id} - Отримати лікаря за ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT', 'DOCTOR')")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Long id) {
        DoctorDTO doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(doctor);
    }

    /**
     * GET /api/doctors/email/{email} - Отримати лікаря за email
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT', 'DOCTOR')")
    public ResponseEntity<DoctorDTO> getDoctorByEmail(@PathVariable String email) {
        DoctorDTO doctor = doctorService.getDoctorByEmail(email);
        return ResponseEntity.ok(doctor);
    }

    /**
     * GET /api/doctors/specialization/{specialization} - Лікарі за спеціалізацією
     */
    @GetMapping("/specialization/{specialization}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT', 'DOCTOR')")
    public ResponseEntity<List<DoctorDTO>> getDoctorsBySpecialization(
            @PathVariable String specialization) {
        List<DoctorDTO> doctors = doctorService.getDoctorsBySpecialization(specialization);
        return ResponseEntity.ok(doctors);
    }

    /**
     * GET /api/doctors/search?name=... - Пошук лікарів за ім'ям
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT', 'DOCTOR')")
    public ResponseEntity<List<DoctorDTO>> searchDoctorsByName(
            @RequestParam String name) {
        List<DoctorDTO> doctors = doctorService.searchDoctorsByName(name);
        return ResponseEntity.ok(doctors);
    }

    /**
     * PUT /api/doctors/{id} - Оновити лікаря
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<DoctorDTO> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDoctorDTO dto) {
        DoctorDTO updated = doctorService.updateDoctor(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/doctors/{id} - Видалити лікаря
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
}
