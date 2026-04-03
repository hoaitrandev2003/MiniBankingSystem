package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.dto.TransferRequestDTO;
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

    @Override
    @Transactional
    public void transferMoney(TransferRequestDTO request){
        // 1. Tìm tài khoản
        BankAccountEntity fromAccount = accountRepository.findByAccountNumber(String.valueOf(request.getFromAccountNumber()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người gửi: " + request.getFromAccountNumber()));

        BankAccountEntity toAccount = accountRepository.findByAccountNumber(String.valueOf(request.getToAccountNumber()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người nhận: " + request.getToAccountNumber()));

        // Lấy số tiền cần chuyển
        double transferAmount = request.getAmount().doubleValue();

        // 2. Kiểm tra số dư (Dùng toán tử so sánh < bình thường cho double)
        if (fromAccount.getBalance() < transferAmount) {
            throw new RuntimeException("Số dư không đủ để thực hiện giao dịch!");
        }

        // 3. Thực hiện chuyển tiền
        fromAccount.setBalance(fromAccount.getBalance() - transferAmount);
        toAccount.setBalance(toAccount.getBalance() + transferAmount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // 4. Lưu giao dịch (Dùng TransactionEntity theo chuẩn mới của Lead)
        TransactionEntity tx = new TransactionEntity();
        tx.setFromAccount(fromAccount); // Thường Lead sẽ để quan hệ Object thay vì Id
        tx.setToAccount(toAccount);
        tx.setAmount(Double.valueOf(transferAmount)); // Nếu bảng Transaction dùng BigDecimal
        tx.setCreatedAt(LocalDateTime.now());
        tx.setStatus("SUCCESS");

        transactionRepository.save(tx);
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
