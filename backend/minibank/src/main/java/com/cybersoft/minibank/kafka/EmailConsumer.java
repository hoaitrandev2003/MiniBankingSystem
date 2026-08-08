package com.cybersoft.minibank.kafka;

import com.cybersoft.minibank.dto.EmailMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {
    @Autowired
    private JavaMailSender mailSender;

    @KafkaListener(topics = "banking-email-verification", groupId = "banking-group")
    public void consumeEmailVerification(EmailMessageDTO message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(message.getToEmail());
            mailMessage.setSubject(message.getSubject());
            mailMessage.setText(message.getBody());

            mailSender.send(mailMessage);
            System.out.println("Đã gửi mã OTP thành công tới: " + message.getToEmail());
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi mail: " + e.getMessage());
        }
    }
}
