package com.cybersoft.minibank.service;

import com.cybersoft.minibank.dto.TransferRequestDTO;

import java.math.BigDecimal;

public interface BankAccountService {
    String deposit(String accountNumber, BigDecimal amount, String description);
    double getAccountBalance(String accountNumber);
    void transferMoney(TransferRequestDTO request);
}
