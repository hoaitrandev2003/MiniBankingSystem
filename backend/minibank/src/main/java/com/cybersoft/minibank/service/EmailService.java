package com.cybersoft.minibank.service;

import com.cybersoft.minibank.entity.EmailDetailsEntity;

public interface EmailService {
    // Method to send simple email
    String sendSimpleMail(String email,String otp);

    // Method to send email with attachment
    String sendMailWithAttachment(EmailDetailsEntity details);
}
