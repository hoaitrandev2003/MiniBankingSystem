package com.cybersoft.minibank.service;

import com.cybersoft.minibank.dto.TransferRequestDTO;
import com.cybersoft.minibank.entity.BankAccount;

import java.math.BigDecimal;
import java.util.Optional;

public interface BankAccountService {
    String deposit(String accountNumber, BigDecimal amount, String description);
    String createAccount(int userId, String accountType);
    void transferMoney(TransferRequestDTO request);
}
