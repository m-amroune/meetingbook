
package com.mamroune.meetingbook.service;

import com.mamroune.meetingbook.domain.AppUser;
import com.mamroune.meetingbook.dto.RegisterRequest;
import com.mamroune.meetingbook.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegistrationServiceTest {

    private final AppUserRepository repository =
            mock(AppUserRepository.class);

    private final PasswordEncoder passwordEncoder =
            mock(PasswordEncoder.class);

    private final RegistrationService service =
            new RegistrationService(repository, passwordEncoder);

    @Test
    void registersUserWithHashedPassword() {
        RegisterRequest request =
                new RegisterRequest(" Test@Example.com ", "SecurePassword123!");

        when(passwordEncoder.encode("SecurePassword123!"))
                .thenReturn("hashed-password");

        AppUser savedUser = mock(AppUser.class);
        when(savedUser.getId()).thenReturn(1L);
        when(repository.save(any(AppUser.class))).thenReturn(savedUser);

        Long id = service.register(request);

        assertEquals(1L, id);

        var captor = org.mockito.ArgumentCaptor.forClass(AppUser.class);
        verify(repository).save(captor.capture());

        assertEquals("test@example.com", captor.getValue().getEmail());
        assertEquals("hashed-password", captor.getValue().getPasswordHash());
    }

    @Test
    void rejectsDuplicateEmail() {
        RegisterRequest request =
                new RegisterRequest("test@example.com", "SecurePassword123!");

        when(repository.existsByEmail("test@example.com"))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.register(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(repository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }
}

