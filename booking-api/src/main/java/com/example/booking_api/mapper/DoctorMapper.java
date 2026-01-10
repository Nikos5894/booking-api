package com.example.booking_api.mapper;

import com.example.booking_api.dto.request.CreateDoctorDTO;
import com.example.booking_api.dto.request.UpdateDoctorDTO;
import com.example.booking_api.dto.response.DoctorDTO;
import com.example.booking_api.entity.Doctor;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {

    public Doctor toEntity(CreateDoctorDTO dto) {
        Doctor doctor = new Doctor();
        doctor.setDoctorName(dto.getDoctorName());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setEmail(dto.getEmail());
        doctor.setPhone(dto.getPhone());
        return doctor;
    }

    public void updateEntity(Doctor doctor, UpdateDoctorDTO dto) {
        if (dto.getDoctorName() != null) {
            doctor.setDoctorName(dto.getDoctorName());
        }
        if (dto.getSpecialization() != null) {
            doctor.setSpecialization(dto.getSpecialization());
        }
        if (dto.getEmail() != null) {
            doctor.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            doctor.setPhone(dto.getPhone());
        }
    }

    public DoctorDTO toDTO(Doctor doctor) {
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
