package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.entity.BankAccountEntity;
import com.cybersoft.minibank.entity.TransactionEntity;
import com.cybersoft.minibank.repository.BankAccountRepository;
import com.cybersoft.minibank.repository.TransactionRepository;
import com.cybersoft.minibank.service.BankAccountService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class BankAccountServiceImp implements BankAccountService {
    @Autowired
    private BankAccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;


    //Nạp tiền
    @Override
    @Transactional
    public String deposit(String accountNumber, BigDecimal amount, String description) {
        // 1. Kiểm tra số tiền hợp lệ
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "Số tiền nạp phải lớn hơn 0";
        }

        // 2. Tìm tài khoản
        BankAccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy số tài khoản"));

        // 3. Cập nhật số dư
        double newBalance = account.getBalance() + amount.doubleValue();
        account.setBalance(newBalance);
        accountRepository.save(account);

        // 4. Lưu lịch sử giao dịch
        TransactionEntity transaction = new TransactionEntity();
        transaction.setToAccount(account);
        transaction.setAmount(amount.doubleValue());
        transaction.setTransactionType("DEPOSIT");
        transaction.setDescription(description);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        return "Nạp tiền thành công. Số dư hiện tại: " + newBalance;
    }

    //Lấy số dư
    @Override
    public double getAccountBalance(String accountNumber) {
        BankAccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        return account.getBalance() ;
    }
}
