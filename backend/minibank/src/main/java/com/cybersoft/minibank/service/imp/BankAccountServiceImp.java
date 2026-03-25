package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.entity.BankAccount;
import com.cybersoft.minibank.entity.Transaction;
import com.cybersoft.minibank.repository.BankAccountRepository;
import com.cybersoft.minibank.repository.TransactionRepository;
import com.cybersoft.minibank.service.BankAccountService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BankAccountServiceImp implements BankAccountService {
    @Autowired
    private BankAccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    //Tạo tài khoản

    //Nạp tiền
    @Override
    @Transactional
    public String deposit(String accountNumber, BigDecimal amount,String description) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Số tiền nạp phải lớn hơn 0!");
        }

        BankAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction tx = new Transaction();
        tx.setToAccountId(account.getId());
        tx.setAmount(amount);
        tx.setTransactionType("DEPOSIT");
        tx.setStatus("SUCCESS");
        transactionRepository.save(tx);

        return "Nạp tiền thành công!";
    }


    @Override
    @Transactional
    public String createAccount(int userId, String accountType) {
        // 1. Kiểm tra User có tồn tại không (nếu cần)

        // 2. Sinh số tài khoản ngẫu nhiên hoặc theo quy tắc (VD: 10 chữ số)
        String newAccountNumber = generateUniqueAccountNumber();

        // 3. Khởi tạo đối tượng BankAccount
        BankAccount account = new BankAccount();
        account.setUserId(userId);
        account.setAccountNumber(newAccountNumber);
        account.setBalance(BigDecimal.ZERO); // QUAN TRỌNG: Luôn để mặc định là 0
        account.setAccount_type(accountType); // VD: SAVING, CURRENT
        account.setStatus("ACTIVE");

        accountRepository.save(account);

        return "Tạo tài khoản thành công! Số TK của bạn là: " + newAccountNumber;
    }






    private String generateUniqueAccountNumber() {
        String accountNumber;
        boolean exists;

        do {
            // Tạo chuỗi 10 số ngẫu nhiên
            long number = (long) (Math.random() * 9_000_000_000L) + 1_000_000_000L;
            accountNumber = String.valueOf(number);

            // Kiểm tra xem số này đã có trong database chưa
            exists = accountRepository.existsByAccountNumber(accountNumber);

        } while (exists); // Nếu trùng thì lặp lại để lấy số khác

        return accountNumber;
    }
}
