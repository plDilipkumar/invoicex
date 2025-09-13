package com.example.invoicex.service;

import com.example.invoicex.dto.*;
import com.example.invoicex.entity.User;
import com.example.invoicex.repository.UserRepository;
import com.example.invoicex.security.CustomUserDetails;
import com.example.invoicex.security.CustomUserDetailsService;
import com.example.invoicex.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final MailService mailService;

    public UserDTO register(UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User u = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole() == null ? "ROLE_USER" : dto.getRole())
                .build();
        User saved = userRepository.save(u);
        dto.setId(saved.getId());
        dto.setPassword(null); // do not return password
        return dto;
    }

    public AuthResponse login(AuthRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new RuntimeException("Invalid credentials");
        }

        var userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(req.getUsername());
        String token = jwtUtil.generateToken(userDetails);
        return AuthResponse.builder().token(token).username(userDetails.getUsername()).build();
    }

    @Transactional
    public void initiatePasswordReset(String username) {
        var userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return; // avoid user enumeration
        String temp = Long.toHexString(System.currentTimeMillis());
        User u = userOpt.get();
        u.setPassword(passwordEncoder.encode(temp));
        userRepository.save(u);
        mailService.sendText(username, "Password Reset", "Your temporary password is: " + temp + "\nPlease login and change it immediately.");
    }

    @Transactional
    public void resetPassword(String username, String newPassword) {
        var user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
