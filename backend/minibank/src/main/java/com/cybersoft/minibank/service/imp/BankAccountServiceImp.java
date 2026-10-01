package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.dto.EmailMessageDTO;
import com.cybersoft.minibank.dto.TransferRequestDTO;
import com.cybersoft.minibank.entity.BankAccountEntity;
import com.cybersoft.minibank.entity.TransactionEntity;
import com.cybersoft.minibank.kafka.producer.TransferProducer;
import com.cybersoft.minibank.repository.BankAccountRepository;
import com.cybersoft.minibank.repository.TransactionRepository;
import com.cybersoft.minibank.service.BankAccountService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class BankAccountServiceImp implements BankAccountService {
    @Autowired
    private BankAccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransferProducer transferProducer;
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public BankAccountServiceImp(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

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
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        // 4. Lưu lịch sử giao dịch
        String randomCode = generateRandomAlphaNumeric(8);
        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionCode(randomCode);
        transaction.setToAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionType("DEPOSIT");
        transaction.setStatus("SUCCESS");
        transaction.setDescription(description);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        return "Nạp tiền thành công. Số dư hiện tại: " + newBalance;
    }

    //Lấy số dư
    @Override
    public BigDecimal getAccountBalance(String accountNumber) {
        BankAccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        return account.getBalance() ;
    }

    // Chuyển tiền vào tài khoản khac
    @Override
    @Transactional
    public void transferMoney(TransferRequestDTO request){
        // 1. Tìm tài khoản
        BankAccountEntity fromAccount = accountRepository.findForUpdate(String.valueOf(request.getFromAccountNumber()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người gửi: " + request.getFromAccountNumber()));

        BankAccountEntity toAccount = accountRepository.findByAccountNumber(String.valueOf(request.getToAccountNumber()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người nhận: " + request.getToAccountNumber()));

        // Lấy số tiền cần chuyển
        BigDecimal transferAmount = request.getAmount();
        String description = request.getDescription();

        // Kiểm tra số dư (Dùng toán tử so sánh < bình thường cho double)
        if (fromAccount.getBalance().compareTo(transferAmount) < 0) {
            throw new RuntimeException(
                    "Số dư không đủ"
            );
        }

        // Lưu giao dịch (Dùng TransactionEntity theo chuẩn mới của Lead)
        String randomCode = generateRandomAlphaNumeric(8);
        String otp = String.format("%06d", new SecureRandom().nextInt(999999));

        TransactionEntity tx = new TransactionEntity();
        tx.setTransactionCode(randomCode);
        tx.setFromAccount(fromAccount); // Thường Lead sẽ để quan hệ Object thay vì Id
        tx.setToAccount(toAccount);
        tx.setAmount(transferAmount); // Nếu bảng Transaction dùng BigDecimal
        tx.setTransactionType("TRANSFER");
        tx.setStatus("PENDING");
        tx.setDescription(description);
        tx.setCreatedAt(LocalDateTime.now());
        tx.setOtp(otp);
        tx.setOtpExpiredAt(LocalDateTime.now().plusMinutes(5));

        transactionRepository.save(tx);
        transferProducer.sendTransferSuccess(tx);
    }
}
