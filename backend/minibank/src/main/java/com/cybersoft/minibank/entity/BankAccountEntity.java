package com.cybersoft.minibank.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity(name = "bank_accounts")
public class BankAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String accountNumber;
    private double balance;
    private String currency;
    private String accountType;
    private double dailyTransferLimit;
    private int version;
    private String status;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "fromAccount")
    @JsonManagedReference
    private List<TransactionEntity> sentTransactions;

    @OneToMany(mappedBy = "toAccount")
    @JsonManagedReference
    private List<TransactionEntity> receivedTransactions;

}
