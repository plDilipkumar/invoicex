package com.example.invoicex.service;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private final JavaMailSender mailSender;
    private final Environment env;

    public void sendInvoice(String toEmail, String subject, String body, byte[] pdfBytes, String fileName) {
        if (toEmail == null || toEmail.isBlank()) {
            log.warn("Invoice email skipped because the client has no email address");
            return;
        }
        String apiKey = env.getProperty("app.mail.resend-api-key", "");
        String from = env.getProperty("app.mail.from", "onboarding@resend.dev");
        try {
            if (!apiKey.isBlank()) {
                Map<String, Object> attachment = Map.of("filename", fileName,
                    "content", Base64.getEncoder().encodeToString(pdfBytes));
                Map<String, Object> payload = Map.of(
                    "from", from, "to", List.of(toEmail), "subject", subject,
                    "html", "<div style='font-family:Arial,sans-serif;line-height:1.6;color:#172033'><h2>InvoiceX</h2><p>"
                        + escapeHtml(body) + "</p><p>Your invoice PDF is attached to this email.</p></div>",
                    "attachments", List.of(attachment));
                RestClient.create("https://api.resend.com").post().uri("/emails")
                    .contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + apiKey)
                    .body(payload).retrieve().toBodilessEntity();
                log.info("Invoice email accepted by Resend for recipient {} (attachment {})", maskEmail(toEmail), fileName);
                return;
            }
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            if (!from.isBlank()) helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, false);
            helper.addAttachment(fileName, new ByteArrayResource(pdfBytes));
            mailSender.send(message);
            log.info("Invoice email sent via SMTP to {}", maskEmail(toEmail));
        } catch (Exception e) {
            log.error("Invoice email delivery failed for {}. Configure RESEND_API_KEY and a verified MAIL_FROM address in Render. Reason: {}",
                maskEmail(toEmail), e.getMessage());
        }
    }

    public void sendText(String toEmail, String subject, String body) {
        if (toEmail == null || toEmail.isBlank()) {
            log.warn("Text email skipped because recipient is empty");
            return;
        }
        String apiKey = env.getProperty("app.mail.resend-api-key", "");
        String from = env.getProperty("app.mail.from", "onboarding@resend.dev");
        try {
            if (!apiKey.isBlank()) {
                Map<String, Object> payload = Map.of("from", from, "to", List.of(toEmail), "subject", subject, "text", body);
                RestClient.create("https://api.resend.com").post().uri("/emails")
                    .contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + apiKey)
                    .body(payload).retrieve().toBodilessEntity();
            } else {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
                if (!from.isBlank()) helper.setFrom(from);
                helper.setTo(toEmail);
                helper.setSubject(subject);
                helper.setText(body, false);
                mailSender.send(message);
            }
            log.info("Text email accepted for recipient {}", maskEmail(toEmail));
        } catch (Exception e) {
            log.error("Email delivery failed for {}: {}", maskEmail(toEmail), e.getMessage());
        }
    }

    private String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 1) return "***";
        return email.substring(0, 1) + "***" + email.substring(at);
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
            .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
