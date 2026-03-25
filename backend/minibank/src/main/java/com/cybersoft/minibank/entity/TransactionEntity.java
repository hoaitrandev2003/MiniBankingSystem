package com.cybersoft.minibank.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_code", unique = true)
    private String transactionCode;

    @Column(name = "from_account_id")
    private Integer fromAccountId;

    @Column(name = "to_account_id")
    private Integer toAccountId;

    private BigDecimal amount;

    @Column(name = "transaction_type")
    private String transactionType; //"DEPOSIT", "WITHDRAW", "TRANSFER"

    @Column(name = "transaction_categories_id")
    private Integer transactionCategoryId;

    private String status;  // "SUCCESS", "FAILED", "PENDING"
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
