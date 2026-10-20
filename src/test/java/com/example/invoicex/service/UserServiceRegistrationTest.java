package com.example.invoicex.service;

import com.example.invoicex.dto.UserDTO;
import com.example.invoicex.entity.User;
import com.example.invoicex.repository.UserRepository;
import com.example.invoicex.security.CustomUserDetailsService;
import com.example.invoicex.security.JwtUtil;
import com.example.invoicex.service.MailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceRegistrationTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtUtil jwtUtil;
    @Mock CustomUserDetailsService userDetailsService;
    @Mock MailService mailService;

    @InjectMocks UserService userService;

    @Test
    void publicRegistrationAlwaysAssignsUserRoleAndReturnsSafePersistedFields() {
        UserDTO request = UserDTO.builder()
                .username("candidate")
                .email("candidate@example.com")
                .password("StrongPassword123!")
                .role("ROLE_ADMIN")
                .build();

        when(userRepository.existsByUsername("candidate")).thenReturn(false);
        when(userRepository.existsByEmail("candidate@example.com")).thenReturn(false);
        when(passwordEncoder.encode("StrongPassword123!")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });

        UserDTO response = userService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("ROLE_USER", userCaptor.getValue().getRole());
        assertEquals("ROLE_USER", response.getRole());
        assertEquals(42L, response.getId());
        assertEquals("candidate", response.getUsername());
        assertEquals("candidate@example.com", response.getEmail());
        assertNull(response.getPassword());
    }
}
