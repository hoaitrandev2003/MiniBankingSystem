package com.cybersoft.minibank.service;

import com.cybersoft.minibank.dto.TransferRequestDTO;
import com.cybersoft.minibank.payload.request.TransferComfirmRequest;

import java.math.BigDecimal;

public interface BankAccountService {
    String deposit(String accountNumber, BigDecimal amount, String description);
    BigDecimal getAccountBalance(String accountNumber);
    String transferMoney(TransferRequestDTO request);
    void confirmTransfer(TransferComfirmRequest comfirmRequest);
}
