package com.example.booking_api.service;

import com.example.booking_api.dto.request.LoginRequest;
import com.example.booking_api.dto.request.RegisterRequest;
import com.example.booking_api.dto.response.AuthResponse;
import com.example.booking_api.entity.Doctor;
import com.example.booking_api.entity.Patient;
import com.example.booking_api.entity.Role;
import com.example.booking_api.entity.User;
import com.example.booking_api.exception.ResourceAlreadyExistsException;
import com.example.booking_api.repository.DoctorRepository;
import com.example.booking_api.repository.PatientRepository;
import com.example.booking_api.repository.UserRepository;
import com.example.booking_api.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }


        // Створюємо User
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(request.getRoles())
                .enabled(true)
                .build();

        user = userRepository.save(user);


        if (request.getRoles().contains(Role.DOCTOR)) {
            Doctor doctor = Doctor.builder()
                    .user(user)
                    .doctorName(request.getFullName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .specialization(request.getSpecialization())
                    .createdAt(LocalDateTime.now())
                    .build();
            doctorRepository.save(doctor);
        }

        if (request.getRoles().contains(Role.PATIENT)) {
            Patient patient = Patient.builder()
                    .user(user)
                    .patientName(request.getFullName())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhone())
                    .createdAt(LocalDateTime.now())
                    .build();
            patientRepository.save(patient);
        }

        String token = jwtUtils.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtUtils.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }
}

