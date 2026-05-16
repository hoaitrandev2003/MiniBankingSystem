package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.UserCreatedEvent;
import com.cybersoft.minibank.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class KafkaProducerServiceImp implements KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void sendUserCreatedEvent(UserCreatedEvent event) {
        String json =
                objectMapper.writeValueAsString(event);
        kafkaTemplate.send("password-mail-topic", json);
    }
}
