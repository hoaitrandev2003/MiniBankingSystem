package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.entity.EmailDetailsEntity;
import com.cybersoft.minibank.service.EmailService;
import com.cybersoft.minibank.service.OtpService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailServiceImp implements EmailService {
    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private OtpService otpService;

    // Gửi mail với văn bản đơn giản đến người nhận mong muốn
    @Override
    public String sendSimpleMail(String email,String otp) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(sender);
            mailMessage.setSubject("Your OTP Code");
            mailMessage.setText("Your OTP code is: " + otp + "\nValid for 5 minutes.");

            javaMailSender.send(mailMessage);

            return "Mail Sent Successfully";

        } catch (Exception e) {

            return "Error while sending mail";
        }
    }

    // Gửi mail đính kèm tệp
    @Override
    public String sendMailWithAttachment(EmailDetailsEntity details) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {

            helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(sender);
            helper.setTo(details.getRecipient());
            helper.setText(details.getMsgBody());
            helper.setSubject(details.getSubject());

            FileSystemResource file = new FileSystemResource(new File(details.getAttachment()));

            helper.addAttachment(file.getFilename(), file);

            javaMailSender.send(mimeMessage);

            return "Mail Sent Successfully";

        } catch (MessagingException e) {

            return "Error while sending mail";
        }
    }
}
