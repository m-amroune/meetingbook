package com.mamroune.meetingbook.service;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mamroune.meetingbook.domain.AppUser;
import com.mamroune.meetingbook.dto.RegisterRequest;
import com.mamroune.meetingbook.repository.AppUserRepository;

@Service
public class RegistrationService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Long register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (appUserRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Un compte existe déjà avec cet email"
            );
        }

        String passwordHash = passwordEncoder.encode(request.password());

        AppUser user = new AppUser(email, passwordHash);

        return appUserRepository.save(user).getId();
    }
}
