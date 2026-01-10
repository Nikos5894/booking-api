package com.example.booking_api.mapper;

import com.example.booking_api.dto.request.CreatePatientDTO;
import com.example.booking_api.dto.request.UpdatePatientDTO;
import com.example.booking_api.dto.response.PatientDTO;
import com.example.booking_api.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public Patient toEntity(CreatePatientDTO dto) {
        Patient patient = new Patient();
        patient.setPatientName(dto.getPatientName());
        patient.setEmail(dto.getEmail());
        patient.setPhoneNumber(dto.getPhoneNumber());
        return patient;
    }
    public Patient toEntity(UpdatePatientDTO dto) {
        Patient patient = new Patient();
        updatePatientFields(patient, dto);
        return patient;
    }

    public void updateEntity(Patient patient, UpdatePatientDTO dto) {
        updatePatientFields(patient, dto);
    }

    public PatientDTO toDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setPatientName(patient.getPatientName());
        dto.setEmail(patient.getEmail());
        dto.setPhoneNumber(patient.getPhoneNumber());
        dto.setCreatedAt(patient.getCreatedAt());
        dto.setUpdatedAt(patient.getUpdatedAt());
        return dto;
    }

    private void updatePatientFields(Patient patient, UpdatePatientDTO dto) {
        if (dto.getPatientName() != null) {
            patient.setPatientName(dto.getPatientName());
        }
        if (dto.getEmail() != null) {
            patient.setEmail(dto.getEmail());
        }
        if (dto.getPhoneNumber() != null) {
            patient.setPhoneNumber(dto.getPhoneNumber());
        }
    }
}
