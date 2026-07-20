package com.gaja.clinic.service;

import com.gaja.clinic.entity.Doctor;
import com.gaja.clinic.entity.User;
import com.gaja.clinic.security.CustomUserDetails;

import java.util.Optional;

public interface AuthService {

    Optional<User> validateCredentials(String login, String password);

    Optional<CustomUserDetails> getCurrentUser();

    Doctor requireCurrentDoctor();
}
