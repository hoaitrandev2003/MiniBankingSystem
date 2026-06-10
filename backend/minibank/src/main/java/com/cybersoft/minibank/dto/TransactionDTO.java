package com.cybersoft.minibank.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionDTO {
    private LocalDateTime createdAt;
    private String description;
    private String status;
    private BigDecimal amount;
}
