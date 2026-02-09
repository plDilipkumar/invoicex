package com.example.invoicex.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final org.springframework.core.env.Environment env;

    public void sendInvoice(String toEmail, String subject, String body, byte[] pdfBytes, String fileName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String from = env.getProperty("app.mail.from");
            if (from != null && !from.isEmpty()) helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, false);
            helper.addAttachment(fileName, new ByteArrayResource(pdfBytes));
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Email send failed: " + e.getMessage());
        }
    }

    public void sendText(String toEmail, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);
            String from = env.getProperty("app.mail.from");
            if (from != null && !from.isEmpty()) helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, false);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Email send failed: " + e.getMessage());
        }
    }
}


