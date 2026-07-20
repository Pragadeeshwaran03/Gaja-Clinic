package com.gaja.clinic.service.impl;

import com.gaja.clinic.model.ClinicSettings;
import com.gaja.clinic.service.EmailService;
import com.gaja.clinic.service.SettingsService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final SettingsService settingsService;

    @Override
    public void sendEmail(String toEmail, String subject, String htmlBody, String attachmentPath) {
        ClinicSettings clinic = settingsService.getSettings();

        if (clinic.getSmtpHost() == null || clinic.getSmtpHost().isBlank()) {
            throw new IllegalStateException("SMTP host is not configured. Please set it in Clinic Settings.");
        }

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(clinic.getSmtpHost());
        mailSender.setPort(clinic.getSmtpPort());
        mailSender.setUsername(clinic.getSmtpUser());
        mailSender.setPassword(clinic.getSmtpPassword());

        var props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(clinic.isSmtpEnableSsl()));

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String fromEmail = clinic.getSmtpFromEmail() != null && !clinic.getSmtpFromEmail().isBlank()
                    ? clinic.getSmtpFromEmail() : clinic.getSmtpUser();
            String fromName = clinic.getSmtpFromName() != null && !clinic.getSmtpFromName().isBlank()
                    ? clinic.getSmtpFromName() : clinic.getClinicName();

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            if (attachmentPath != null && !attachmentPath.isBlank()) {
                Path path = Path.of(attachmentPath);
                if (Files.exists(path)) {
                    helper.addAttachment(path.getFileName().toString(), new FileSystemResource(path.toFile()));
                }
            }

            mailSender.send(message);
            log.info("Email sent to {} with subject '{}'", toEmail, subject);
        } catch (Exception ex) {
            log.error("SMTP error sending email to {}", toEmail, ex);
            throw new IllegalStateException("Email could not be sent: " + ex.getMessage(), ex);
        }
    }
}
