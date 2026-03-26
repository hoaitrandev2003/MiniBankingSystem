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

    //Tạo tài khoản
//    @Override
//    @Transactional
//    public String createAccount(int userId, String accountType) {
//        // 1. Kiểm tra User tồn tại
//        if (!userRepository.existsById(userId)) {
//            return "Người dùng không tồn tại";
//        }
//
//        // 2. Sinh số tài khoản ngẫu nhiên (Ví dụ 10 chữ số)
//        String newAccountNumber = "VNB" + (long) (Math.random() * 10000000000L);
//
//        // 3. Khởi tạo thực thể
//        BankAccountEntity newAccount = new BankAccountEntity();
//        newAccount.setAccountNumber(newAccountNumber);
//        newAccount.setAccountType(accountType);
//        newAccount.setBalance(0.0);
//        newAccount.setCurrency("VND");
//        newAccount.setStatus("ACTIVE");
//        newAccount.setCreatedAt(LocalDateTime.now());
//        // Giả sử bạn có trường user trong BankAccountEntity để liên kết
//        // newAccount.setUser(userRepository.findById(userId).get());
//
//        accountRepository.save(newAccount);
//        return "Tạo tài khoản thành công: " + newAccountNumber;
//    }



}
