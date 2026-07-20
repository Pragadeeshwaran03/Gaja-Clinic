package com.gaja.clinic.service;

public interface EmailService {

    void sendEmail(String toEmail, String subject, String htmlBody, String attachmentPath);
}
