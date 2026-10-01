package com.cybersoft.minibank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferEvent(
        int transactionId,
        String transactionCode,
        String transactionType,
        String fromAccount,
        String toAccount,
        BigDecimal amount,
        String status,
        LocalDateTime createdAt
        ) {
}
