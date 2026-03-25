package com.cybersoft.minibank.service;

import java.math.BigDecimal;

public interface BankAccountService {
    String deposit(String accountNumber, BigDecimal amount, String description);

    String createAccount(int userId, String accountType);
}
