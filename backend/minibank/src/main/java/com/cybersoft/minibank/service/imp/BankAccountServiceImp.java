package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.dto.TransferRequestDTO;
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
    }

    ///  chuyen tien
    @Override
    @Transactional
    public void transferMoney(TransferRequestDTO request){
        // 1. Tìm tài khoản
        BankAccountEntity fromAccount = accountRepository.findByAccountNumber(String.valueOf(request.getFromId()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người gửi: " + request.getFromId()));

        BankAccountEntity toAccount = accountRepository.findByAccountNumber(String.valueOf(request.getToId()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người nhận: " + request.getToId()));

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
        tx.setAmount(BigDecimal.valueOf(transferAmount)); // Nếu bảng Transaction dùng BigDecimal
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
