package com.example.invoicex.controller;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final DataSource dataSource;
    private final Environment environment;

    public HealthController(DataSource dataSource, Environment environment) {
        this.dataSource = dataSource;
        this.environment = environment;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        String apiKey = environment.getProperty("app.mail.resend-api-key", "");
        String smtpUser = environment.getProperty("spring.mail.username", "");
        String smtpPassword = environment.getProperty("spring.mail.password", "");
        String mailStatus = !apiKey.isBlank() ? "resend-configured"
                : (!smtpUser.isBlank() && !smtpPassword.isBlank() ? "smtp-configured" : "needs-configuration");
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT 1");
             ResultSet result = statement.executeQuery()) {
            if (result.next() && result.getInt(1) == 1) {
                return ResponseEntity.ok(Map.of("status", "ok", "database", "connected", "mail", mailStatus));
            }
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("status", "degraded", "database", "unavailable", "mail", mailStatus));
        } catch (SQLException exception) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("status", "degraded", "database", "unavailable", "mail", mailStatus));
        }
    }
}
