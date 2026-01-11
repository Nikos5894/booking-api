package com.example.booking_api.controller;


import com.example.booking_api.dto.request.CreatePatientDTO;
import com.example.booking_api.dto.request.UpdatePatientDTO;
import com.example.booking_api.dto.response.PatientDTO;
import com.example.booking_api.entity.Patient;
import com.example.booking_api.entity.Role;
import com.example.booking_api.entity.User;
import com.example.booking_api.service.PatientService;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;


import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    /**
     * POST /api/patients - Створити пацієнта
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody CreatePatientDTO dto) {
        PatientDTO created = patientService.createPatient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /api/patients - Отримати всіх пацієнтів
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    /**
     * GET /api/patients/{id} - Отримати пацієнта за ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<PatientDTO> getPatientById(
            @PathVariable Long id,
            Authentication authentication) {  // ✅ Параметр коректний

        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRoles().contains(Role.PATIENT) &&
                !currentUser.getRoles().contains(Role.ADMIN)) {
            Patient patient = patientService.findByUserId(currentUser.getId());
            if (!patient.getId().equals(id)) {
                throw new AccessDeniedException("Access denied");
            }
        }

        PatientDTO patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }


    /**
     * GET /api/patients/email/{email} - Отримати пацієнта за email
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<PatientDTO> getPatientByEmail(@PathVariable String email) {
        PatientDTO patient = patientService.getPatientByEmail(email);
        return ResponseEntity.ok(patient);
    }

    /**
     * GET /api/patients/phone/{phoneNumber} - Отримати пацієнта за телефоном
     */
    @GetMapping("/phone/{phoneNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<PatientDTO> getPatientByPhoneNumber(@PathVariable String phoneNumber) {
        PatientDTO patient = patientService.getPatientByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(patient);
    }

    /**
     * GET /api/patients/search?name=... - Пошук пацієнтів за ім'ям
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<PatientDTO>> searchPatientsByName(@RequestParam String name) {
        List<PatientDTO> patients = patientService.searchPatientsByName(name);
        return ResponseEntity.ok(patients);
    }

    /**
     * PUT /api/patients/{id} - Оновити пацієнта
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePatientDTO dto,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        if (!currentUser.getRoles().contains(Role.ADMIN)) {
            Patient patient = patientService.findByUserId(currentUser.getId());
            if (!patient.getId().equals(id)) {
                throw new AccessDeniedException("You can only edit your own profile");
            }
        }

        return ResponseEntity.ok(patientService.updatePatient(id, dto));
    }

    /**
     * DELETE /api/patients/{id} - Видалити пацієнта
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
