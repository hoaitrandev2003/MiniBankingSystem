package com.cybersoft.minibank.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@Entity(name = "bank_accounts")
public class BankAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    private int userId;

    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private String account_type;
    private BigDecimal dailyTransferLimit;
    private int version;
    private String status;
    private LocalDateTime createdAt;
}
