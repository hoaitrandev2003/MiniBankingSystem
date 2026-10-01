package com.cybersoft.minibank.kafka.producer;

import com.cybersoft.minibank.TransferEvent;
import com.cybersoft.minibank.entity.TransactionEntity;

import tools.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransferProducer {

    private final KafkaTemplate<String,String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public TransferProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendTransferSuccess(TransactionEntity tx){

        TransferEvent event =
                new TransferEvent(
                        tx.getId(),
                        tx.getTransactionCode(),
                        tx.getTransactionType(),
                        tx.getFromAccount().getAccountNumber(),
                        tx.getToAccount().getAccountNumber(),
                        tx.getAmount(),
                        tx.getStatus(),
                        tx.getCreatedAt()
                );

        try {

            String objEvent = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(
                    "transfer-success-topic",
                    objEvent
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Không thể publish Kafka Event",
                    e
            );
        }
    }
}
