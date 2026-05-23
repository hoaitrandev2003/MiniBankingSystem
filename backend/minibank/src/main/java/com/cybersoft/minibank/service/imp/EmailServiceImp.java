package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.UserCreatedEvent;
import com.cybersoft.minibank.service.EmailService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class EmailServiceImp implements EmailService {
    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @KafkaListener(topics = "password-mail-topic", groupId = "minibank-group")
    public void sendSimpleMail(String message) {
        try {
            UserCreatedEvent event = objectMapper.readValue(message, UserCreatedEvent.class);
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(event.email());
            mailMessage.setSubject("Mã Password tạm của bạn");
            mailMessage.setText("Mã password tạm của bạn là: " + event.password() + "\nHiệu lực trong 5 phút.");

            javaMailSender.send(mailMessage);

        } catch (Exception e) {
            throw new  RuntimeException(e + "Consumer bị lỗi");
        }
    }
}