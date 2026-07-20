package com.gaja.clinic.service.impl;

import com.gaja.clinic.entity.Doctor;
import com.gaja.clinic.entity.User;
import com.gaja.clinic.exception.ResourceNotFoundException;
import com.gaja.clinic.repository.DoctorRepository;
import com.gaja.clinic.repository.UserRepository;
import com.gaja.clinic.security.CustomUserDetails;
import com.gaja.clinic.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> validateCredentials(String login, String password) {
        return userRepository.findByUsernameOrEmail(login)
                .filter(user -> passwordEncoder.matches(password, user.getPasswordHash()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomUserDetails> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return Optional.of(userDetails);
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Doctor requireCurrentDoctor() {
        CustomUserDetails user = getCurrentUser()
                .orElseThrow(() -> new ResourceNotFoundException("Not authenticated"));
        return doctorRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for current user"));
    }
}
