package com.cybersoft.minibank.service;

import com.cybersoft.minibank.UserCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface KafkaProducerService {
    void sendUserCreatedEvent(UserCreatedEvent event) throws JsonProcessingException;
}
