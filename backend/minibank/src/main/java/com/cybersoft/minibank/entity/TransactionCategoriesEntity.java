package com.cybersoft.minibank.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "transaction_categories")
public class TransactionCategoriesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String code;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "transactionCategories")
    private List<TransactionEntity> transactions;
}
