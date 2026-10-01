package com.cybersoft.minibank.kafka.consumer;

import com.cybersoft.minibank.TransferEvent;
import com.cybersoft.minibank.entity.TransactionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferProducer {
    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void sendTransferSuccess(TransactionEntity tx){

        TransferEvent event = TransferEvent.builder()
                        .transactionId(tx.getId())
                        .fromAccount(tx.getFromAccount().getAccountNumber())
                        .toAccount(tx.getToAccount().getAccountNumber())
                        .amount(tx.getAmount())
                        .createdAt(LocalDateTime.now())
                        .build();

        kafkaTemplate.send("transfer-success-topic", event);
    }
}
