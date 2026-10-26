
package com.mamroune.meetingbook.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mamroune.meetingbook.dto.RegisterRequest;
import com.mamroune.meetingbook.service.RegistrationService;

import jakarta.validation.Valid;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegistrationService registrationService;

    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Long>> register(
            @Valid @RequestBody RegisterRequest request) {

        Long userId = registrationService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", userId));
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }

    @GetMapping("/me")
    public Map<String, String> currentUser(Authentication authentication) {
        return Map.of("email", authentication.getName());
    }
}
