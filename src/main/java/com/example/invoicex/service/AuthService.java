package com.example.invoicex.service;

import com.example.invoicex.dto.PasswordResetDTO;
import com.example.invoicex.dto.PasswordResetRequest;
import com.example.invoicex.entity.PasswordResetToken;
import com.example.invoicex.entity.User;
import com.example.invoicex.exception.ResourceNotFoundException;
import com.example.invoicex.repository.PasswordResetTokenRepository;
import com.example.invoicex.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public User getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("No authenticated user");
        }
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + auth.getName()));
    }

    public void requestPasswordReset(PasswordResetRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Delete any existing reset tokens for this user
            passwordResetTokenRepository.deleteByUser(user);
            
            // Create new reset token
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(token)
                    .user(user)
                    .expiryDate(LocalDateTime.now().plusHours(24)) // Token expires in 24 hours
                    .used(false)
                    .build();
            
            passwordResetTokenRepository.save(resetToken);
            
            // Send reset email
            String resetUrl = "http://localhost:3000/reset-password?token=" + token;
            String emailBody = String.format(
                "Hello %s,\n\n" +
                "You requested a password reset for your InvoiceX account.\n\n" +
                "Click the link below to reset your password:\n" +
                "%s\n\n" +
                "This link will expire in 24 hours.\n\n" +
                "If you didn't request this password reset, please ignore this email.\n\n" +
                "Best regards,\n" +
                "InvoiceX Team",
                user.getUsername(),
                resetUrl
            );
            
            try {
                mailService.sendText(user.getEmail(), "Password Reset Request - InvoiceX", emailBody);
            } catch (Exception e) {
                // Log error but don't expose it to user for security
                System.err.println("Failed to send password reset email: " + e.getMessage());
            }
        }
        // Always return success to prevent email enumeration attacks
    }

    public void resetPassword(PasswordResetDTO resetDTO) {
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(resetDTO.getToken());
        
        if (tokenOpt.isEmpty()) {
            throw new ResourceNotFoundException("Invalid or expired reset token");
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        
        if (resetToken.isExpired() || resetToken.isUsed()) {
            throw new ResourceNotFoundException("Invalid or expired reset token");
        }
        
        // Update user password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(resetDTO.getNewPassword()));
        userRepository.save(user);
        
        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}
