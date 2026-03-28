package com.cybersoft.minibank.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "transactions")
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String transactionCode;
    private double amount;
    private String transactionType;
    private String status;
    private String description;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_number")
    @JsonBackReference
    private BankAccountEntity fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_number")
    @JsonBackReference
    private BankAccountEntity toAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_categories_id")
    @JsonBackReference
    private TransactionCategoriesEntity transactionCategories;
}
