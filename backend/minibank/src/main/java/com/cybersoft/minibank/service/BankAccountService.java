package com.cybersoft.minibank.service;

import com.cybersoft.minibank.dto.TransferRequestDTO;

import java.math.BigDecimal;

public interface BankAccountService {
    String deposit(String accountNumber, BigDecimal amount, String description);
    BigDecimal getAccountBalance(String accountNumber);
    void transferMoney(TransferRequestDTO request);
}
