package com.cybersoft.minibank.controller;

import com.cybersoft.minibank.dto.TransferRequestDTO;
import com.cybersoft.minibank.payload.request.TransferComfirmRequest;
import com.cybersoft.minibank.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/accounts")
public class AccountController {

    @Autowired
    private BankAccountService bankAccountService;
    //Nạp tiền
    @PutMapping("/deposit/{accountNumber}")
    public ResponseEntity<?> deposit(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false, defaultValue = "Nạp tiền tại quầy/Online") String description) {

        String result = bankAccountService.deposit(accountNumber, amount, description);

        return ResponseEntity.ok(result);
    }

    //Chuyen tien
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequestDTO request) {
        try {
            String transactionCode = bankAccountService.transferMoney(request);
            // Trả về mã Code để Front-end lưu lại, chuẩn bị gửi kèm mã OTP lên ở bước sau
            return ResponseEntity.ok(Map.of("transactionCode", transactionCode, "message", "Mã OTP đã được gửi qua Email"));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage()); // Trả lỗi có đinh nghĩa

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @PostMapping("/transfer/confirm")
    public ResponseEntity<?> confirmTransfer(@RequestBody TransferComfirmRequest comfirmRequest) {
        try {
            bankAccountService.confirmTransfer(comfirmRequest);
            return ResponseEntity.ok("Xác thực thành công. Giao dịch chuyển tiền hoàn tất!");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    //Lấy số dư
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<?> getBalance(@PathVariable String accountNumber){
        BigDecimal balance =  bankAccountService.getAccountBalance(accountNumber);
        return ResponseEntity.ok("Số dư của tài khoản " + accountNumber + " là: " + balance); }

}
