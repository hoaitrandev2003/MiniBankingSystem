package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.dto.EmailMessageDTO;
import com.cybersoft.minibank.dto.TransferRequestDTO;
import com.cybersoft.minibank.entity.BankAccountEntity;
import com.cybersoft.minibank.entity.TransactionEntity;
import com.cybersoft.minibank.payload.request.TransferComfirmRequest;
import com.cybersoft.minibank.repository.BankAccountRepository;
import com.cybersoft.minibank.repository.TransactionRepository;
import com.cybersoft.minibank.service.BankAccountService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

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

    @Override
    @Transactional
    public String transferMoney(TransferRequestDTO request) {
        // Tìm tài khoản
        BankAccountEntity fromAccount = accountRepository.findByAccountNumber(String.valueOf(request.getFromAccountNumber()))
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

        // Đưa message vào Kafka
        // Lấy email trực tiếp từ thực thể User liên kết với tài khoản gửi
        if (fromAccount.getUserEntity() == null || fromAccount.getUserEntity().getEmail() == null) {
            throw new RuntimeException("Tài khoản người gửi chưa đăng ký email xác thực!");
        }

        String userEmail = fromAccount.getUserEntity().getEmail();

        EmailMessageDTO emailMessage = new EmailMessageDTO(
                userEmail,
                "Xác thực giao dịch chuyển tiền",
                "Mã OTP của bạn là: " + otp + ". Mã này có hiệu lực trong 5 phút."
        );

        // Gửi tới topic tên là "banking-email-verification"
        kafkaTemplate.send("banking-email-verification", emailMessage);

        return randomCode;
    }

    @Override
    public void confirmTransfer(TransferComfirmRequest comfirmRequest) {
        // Tìm giao dịch PENDING dựa vào mã code
        TransactionEntity transaction = transactionRepository.findByTransactionCode(comfirmRequest.getTransactionCode())
                .orElseThrow(() -> new RuntimeException("Giao dịch không tồn tại"));

        if (!"PENDING".equals(transaction.getStatus())) {
            throw new RuntimeException("Giao dịch này đã được xử lý");
        }

        // Kiểm tra OTP
        if (transaction.getOtpExpiredAt().isBefore(LocalDateTime.now())) {
            transaction.setStatus("FAILED");
            transactionRepository.save(transaction);
            throw new RuntimeException("Mã OTP đã hết hạn");
        }

        if (!transaction.getOtp().equals(comfirmRequest.getOtp())) {
            throw new RuntimeException("Mã OTP không chính xác");
        }

        // Thực hiện trừ tiền người gửi và cộng tiền người nhận
        BankAccountEntity fromAccount = transaction.getFromAccount();
        BankAccountEntity toAccount = transaction.getToAccount();
        BigDecimal transferAmount = transaction.getAmount();

        if (fromAccount.getBalance().compareTo(transferAmount) < 0) {
            transaction.setStatus("FAILED");
            transactionRepository.save(transaction);
            throw new RuntimeException("Số dư tài khoản không đủ!");
        }

        // Thực hiện chuyển tiền
        fromAccount.setBalance(fromAccount.getBalance().subtract(transferAmount));
        toAccount.setBalance(toAccount.getBalance().add(transferAmount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Cập nhật trạng thái giao dịch thành SUCCESS
        transaction.setStatus("SUCCESS");
        transactionRepository.save(transaction);
    }

    private String generateRandomAlphaNumeric(int length) {
        String charSet = "0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(charSet.charAt(random.nextInt(charSet.length())));
        }
        return sb.toString();
    }
}
