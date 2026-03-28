package com.cybersoft.minibank.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionDTO {
    private LocalDateTime createdAt;
    private String description;
    private String status;
    private double amount;
}
