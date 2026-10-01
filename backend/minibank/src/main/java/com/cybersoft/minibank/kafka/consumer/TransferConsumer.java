package com.cybersoft.minibank.kafka.consumer;

import com.cybersoft.minibank.TransferEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class TransferConsumer {
    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "transfer-success-topic", groupId = "bank-group")
    public TransferEvent transferEvent(String message) {
        TransferEvent transferEvent = objectMapper.readValue(message, TransferEvent.class);
        return transferEvent;
    }
}
