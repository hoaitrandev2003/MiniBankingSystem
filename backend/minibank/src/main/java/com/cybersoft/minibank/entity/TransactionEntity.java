package com.cybersoft.minibank.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private int id;

    @Column(name = "transaction_code")
    private String transactionCode;
    private BigDecimal amount;

    @Column(name = "transaction_type")
    private String transactionType;
    private String status;
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    private String otp;

    @Column(name = "otp_expired_at")
    private LocalDateTime otpExpiredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    @JsonBackReference
    private BankAccountEntity fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    @JsonBackReference
    private BankAccountEntity toAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_categories_id")
    @JsonBackReference
    private TransactionCategoriesEntity transactionCategories;
}
