package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
@Service
public class EmailServiceImp implements EmailService {
    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    @KafkaListener(topics = "password-mail-topic", groupId = "minibank-group")
    public void sendSimpleMail(String message) { // Đổi String thành void
        System.out.println("===> KAFKA CONSUMER NHẬN ĐƯỢC: " + message);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(message);

            String email = jsonNode.get("email").asText();
            String otp = jsonNode.get("password").asText();

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(email);
            mailMessage.setSubject("Mã Password tạm của bạn");
            mailMessage.setText("Mã password tạm của bạn là: " + otp + "\nHiệu lực trong 5 phút.");

            javaMailSender.send(mailMessage);
            System.out.println("===> GỬI MAIL THÀNH CÔNG TỚI: " + email);

        } catch (Exception e) {
            System.err.println("===> LỖI TẠI CONSUMER: " + e.getMessage());
            e.printStackTrace();
        }
    }
}