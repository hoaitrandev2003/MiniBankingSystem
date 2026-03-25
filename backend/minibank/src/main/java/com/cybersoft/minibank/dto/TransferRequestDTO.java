package com.cybersoft.minibank.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestDTO {
    private int fromId;
    private int toId;
    private BigDecimal amount;
}